package ir.co.sadad.departuretaxapi.controllers;

import ir.co.sadad.departuretaxapi.dtos.*;
import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import ir.co.sadad.departuretaxapi.services.DepartureTaxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "${v1API}/departure")
public class DepartureTaxController {

}
