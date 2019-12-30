package edu.iastate.coms309.cyschedulebackend.controller;


import edu.iastate.coms309.cyschedulebackend.Service.AccountService;
import edu.iastate.coms309.cyschedulebackend.Service.EventService;
import edu.iastate.coms309.cyschedulebackend.exception.event.EventNotFoundException;
import edu.iastate.coms309.cyschedulebackend.persistence.model.Response;
import edu.iastate.coms309.cyschedulebackend.persistence.model.UserInformation;
import edu.iastate.coms309.cyschedulebackend.persistence.requestModel.EventRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.PackedColorModel;
import java.security.Principal;
import java.text.ParseException;


@RestController
@RequestMapping("/api/v1/event")
@Api(tags = "RestAPI Related to TimeBlock")
public class EventController {

    final EventService eventService;

    final AccountService accountService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public EventController(EventService eventService,
                           AccountService accountService) {
        this.eventService = eventService;
        this.accountService = accountService;
    }

    @GetMapping(value = "/all")
    @ApiOperation("Get All TimeBlock")
    public Response getAllEventByUserID(Principal principal, HttpServletRequest request) {
        Response response = new Response();

        eventService.getAllEvent(principal.getName()).forEach(V -> {
            response.addResponse(V.getEventID(), V);
        });

        return response.OK().send(request.getRequestURI());
    }

    @GetMapping(value = "/managed")
    @ApiOperation("Get All TimeBlock")
    public Response getManagedEventByUserID(Principal principal, HttpServletRequest request) {
        Response response = new Response();

        eventService.getAllManagedEvent(principal.getName()).forEach(V -> {
            response.addResponse(V.getEventID(), V);
        });

        return response.OK().send(request.getRequestURI());
    }

    @PostMapping(value = "/add")
    @ApiOperation("add new TimeBlock")
    public Response addNewEvent(Principal principal, HttpServletRequest request,  @Validated EventRequest newEvent) throws ParseException {
        UserInformation userInformation = accountService.getUserInformation(principal.getName());

        eventService.addEvent(newEvent,userInformation);

        logger.debug("A new event for user [" + principal.getName() + "] is success created");

        return new Response()
                .Created()
                .send(request.getRequestURI());
    }

    @ApiOperation("delete timeBlock by id")
    @DeleteMapping(value = "/{eventID}")
    public Response deleteTimeBlock(Principal principal, HttpServletRequest request, @PathVariable String eventID) {
        Response response = new Response();

        // check ownership( will migrate to decision manager)
        if (eventService.checkOwnerShip(eventID, principal.getName()))
            return response.Forbidden().send(request.getRequestURI()).Created();

        eventService.deleteEvent(eventID);
        logger.debug("A new event for user [" + principal.getName() + "] is success deleted");
        return response.noContent().send(request.getRequestURI()).Created();
    }

    @ApiOperation("load timeBlock by id")
    @GetMapping(value = "/{eventID}")
    public Response loadTimeBlock(HttpServletRequest request, @PathVariable String eventID) throws EventNotFoundException {
        return new Response()
                .OK()
                .addResponse("TimeBlock", eventService.getEvent(eventID))
                .send(request.getRequestURI());
    }

    @ApiOperation("update timeBlock by id")
    @PutMapping(value = "/{eventID}")
    public Response updateTimeBlock(Principal principal, HttpServletRequest request, @PathVariable String eventID, @Validated EventRequest newEvent) throws ParseException, EventNotFoundException {
        Response response = new Response();

        // check ownership( will migrate to decision manager)
        if (!eventService.checkOwnerShip(eventID, principal.getName()))
            return response.Forbidden().send(request.getRequestURI()).Created();

        eventService.updateEvent(newEvent, eventID);
        logger.debug("A updated event for user [" + principal.getName() + "] is success updated");
        return response.OK().send(request.getRequestURI());
    }
}