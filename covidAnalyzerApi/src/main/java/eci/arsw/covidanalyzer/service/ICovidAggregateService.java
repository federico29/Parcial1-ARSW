package eci.arsw.covidanalyzer.service;

import eci.arsw.covidanalyzer.model.Result;
import eci.arsw.covidanalyzer.model.ResultType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ICovidAggregateService {

    void aggregateResult(Result result, ResultType type) throws Exception;

    List<Result> getResult(ResultType type) throws Exception;

    void upsertPersonWithMultipleTests(UUID id, ResultType type) throws Exception;

}
