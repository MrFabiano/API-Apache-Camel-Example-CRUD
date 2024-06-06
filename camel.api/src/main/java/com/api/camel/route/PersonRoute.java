package com.api.camel.route;

import com.api.camel.model.Person;
import com.api.camel.service.PersonService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonRoute extends RouteBuilder {

    @Autowired
    private PersonService personService;


    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        rest("/persons")
                .get("/{id}").to("direct:getPerson")
                .post().type(Person.class).to("direct:addPerson")
                .put("/{id}").type(Person.class).to("direct:updatePerson")
                .delete("/{id}").to("direct:deletePerson")
                .get().to("direct:getAllPersons");

        from("direct:getPerson")
                .bean(personService, "getPerson(${header.id})")
                .choice()
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        from("direct:addPerson")
                .bean(personService, "addPerson")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));

        from("direct:updatePerson")
                .bean(personService, "updatePerson(${header.id}, ${body})")
                .choice()
                .when(body().isNotNull())
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        from("direct:deletePerson")
                .bean(personService, "deletePerson(${header.id})")
//                .onException(Exception.class)
//                .handled(true)
//                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
//                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204));

        from("direct:getAllPersons")
                .bean(personService, "getAllPersons")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
