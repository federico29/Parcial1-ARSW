package eci.arsw.covidanalyzer;

import eci.arsw.covidanalyzer.model.Result;
import eci.arsw.covidanalyzer.model.ResultType;
import eci.arsw.covidanalyzer.service.ICovidAggregateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/covid/result")
public class CovidAggregateController {

    @Autowired
            @Qualifier("serviceCovid")
    ICovidAggregateService covidAggregateService;


    //POST
    @RequestMapping(value = "/true-positive", method = RequestMethod.POST)
    public ResponseEntity<?> addTruePositiveResult(@RequestBody Result result) {
        try {
            covidAggregateService.aggregateResult(result, ResultType.TRUE_POSITIVE);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 409: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/true-negative", method = RequestMethod.POST)
    public ResponseEntity<?> addTrueNegativeResult(@RequestBody Result result) {
        try {
            covidAggregateService.aggregateResult(result, ResultType.TRUE_NEGATIVE);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 409: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/false-positive", method = RequestMethod.POST)
    public ResponseEntity<?> addFalsePositiveResult(@RequestBody Result result) {
        try {
            covidAggregateService.aggregateResult(result, ResultType.FALSE_POSITIVE);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 409: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/false-negative", method = RequestMethod.POST)
    public ResponseEntity<?> addFalseNegativeResult(@RequestBody Result result) {
        try {
            covidAggregateService.aggregateResult(result, ResultType.FALSE_NEGATIVE);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 409: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    //GET
    @RequestMapping(value = "/true-positive", method = RequestMethod.GET)
    public ResponseEntity<?> getTruePositiveResult() {
        try {
            return new ResponseEntity<>(covidAggregateService.getResult(ResultType.TRUE_POSITIVE), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 404: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/true-negative", method = RequestMethod.GET)
    public ResponseEntity<?> getTrueNegativeResult() {
        try {
            return new ResponseEntity<>(covidAggregateService.getResult(ResultType.TRUE_NEGATIVE), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 404: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/false-positive", method = RequestMethod.GET)
    public ResponseEntity<?> getFalsePositiveResult() {
        try {
            return new ResponseEntity<>(covidAggregateService.getResult(ResultType.FALSE_POSITIVE), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 404: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/false-negative", method = RequestMethod.GET)
    public ResponseEntity<?> getFalseNegativeResult() {
        try {
            return new ResponseEntity<>(covidAggregateService.getResult(ResultType.FALSE_NEGATIVE), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 404: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //PUT
    @RequestMapping(value = "/persona/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> savePersonaWithMultipleTests(@PathVariable("id") UUID id) {
        try {
            covidAggregateService.upsertPersonWithMultipleTests(id, ResultType.FALSE_NEGATIVE);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("RESULT TYPE ERROR 409: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}