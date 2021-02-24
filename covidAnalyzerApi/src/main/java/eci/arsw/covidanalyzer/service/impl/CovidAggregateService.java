package eci.arsw.covidanalyzer.service.impl;

import eci.arsw.covidanalyzer.model.Result;
import eci.arsw.covidanalyzer.model.ResultType;
import eci.arsw.covidanalyzer.service.ICovidAggregateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service("serviceCovid")
public class CovidAggregateService implements ICovidAggregateService {

    private List<Result> resultList = new CopyOnWriteArrayList<>();

    public CovidAggregateService() {
        resultList.add(new Result(UUID.randomUUID(), ResultType.TRUE_POSITIVE,"Hillyer","Peoples","Genderfluid"
                ,"hpeoples7@hibu.com","2002-01-28T14:35:34Z","2017-02-14T11:13:37Z",false,0.98));

        resultList.add(new Result(UUID.randomUUID(),ResultType.TRUE_POSITIVE, "Federico","Barrios","Bigender"
                ,"hmallaby9@gmpg.org","2006-09-30T15:37:19Z","1981-11-10T10:12:52Z",false,0.61));

        resultList.add(new Result(UUID.randomUUID(), ResultType.FALSE_NEGATIVE, "Esteban","Bernal","Female"
                ,"est13@gmpg.org","2007-03-30T15:35:19Z","1921-11-10T10:12:52Z",false,0.51));
    }

    @Override
    public void aggregateResult(Result result, ResultType type) throws Exception {
        if(result.getResultType() == type){
            resultList.add(result);
        }else{
            throw new Exception("El tipo dado no coincide con el tipo del resultado.");
        }
    }

    @Override
    public List<Result> getResult(ResultType type) throws Exception {
        List<Result> getResults = new CopyOnWriteArrayList<>();
        for(Result result : resultList){
            if(result.getResultType() == type){
                getResults.add(result);
            }
        }
        if(getResults.size() != 0){
            return getResults;
        }else{
            throw new Exception("No hay resultados del tipo dado.");
        }
    }

    @Override
    public void upsertPersonWithMultipleTests(UUID id, ResultType type) throws Exception {
        boolean isInList = false;
        for(Result result : resultList){
            if(result.getId() == id){
                isInList = true;
                result.setResultType(type);
            }
        }
        if(!isInList){
            throw new Exception("No existe un usuario con ese id.");
        }
    }
}
