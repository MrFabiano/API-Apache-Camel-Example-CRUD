package com.api.camel.route;

import com.api.camel.model.Person;
import com.api.camel.service.PersonService;
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
                .bean(personService, "getPerson(${header.id})");

        from("direct:addPerson")
                .bean(personService, "addPerson");

        from("direct:updatePerson")
                .bean(personService, "updatePerson(${header.id}, ${body})");

        from("direct:deletePerson")
                .bean(personService, "deletePerson(${header.id})");

        from("direct:getAllPersons")
                .bean(personService, "getAllPersons");

    }
}
