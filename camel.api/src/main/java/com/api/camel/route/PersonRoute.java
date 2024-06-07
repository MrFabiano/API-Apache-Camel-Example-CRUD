package com.api.camel.route;

import com.api.camel.exception.ResourceNotFoundException;
import com.api.camel.model.Person;
import com.api.camel.service.PersonService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PersonRoute extends RouteBuilder {

    @Autowired
    private PersonService personService;


    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        onException(ResourceNotFoundException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()))
                .setBody().constant(null);

        rest("/persons")
                .get("/{id}").to("direct:getPerson")
                .post().type(Person.class).to("direct:addPerson")
                .put("/{id}").type(Person.class).to("direct:updatePerson")
                .delete("/{id}").to("direct:deletePerson")
                .get().to("direct:getAllPersons");

        from("direct:getPerson")
                .bean(personService, "getPerson(${header.id})")
                .choice()
                .when(body().isNotNull())
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK.value()))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()));

        from("direct:addPerson")
                .bean(personService, "addPerson")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.CREATED.value()));

        from("direct:updatePerson")
                .bean(personService, "updatePerson(${header.id}, ${body})")
                .choice()
                .when(body().isNotNull())
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK.value()))
                .otherwise()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()));

        from("direct:deletePerson")
                .bean(personService, "deletePerson(${header.id})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NO_CONTENT.value()))
                .process(exchange -> {
                    exchange.getMessage().setBody(null);
                });

        from("direct:getAllPersons")
                .bean(personService, "getAllPersons")
                .process(exchange -> {
                    List<?> persons = exchange.getIn().getBody(List.class);
                    if (persons == null || persons.isEmpty()) {
                        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
                        exchange.getMessage().setBody(null);
                    } else {
                        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());
                    }
                });
    }
}