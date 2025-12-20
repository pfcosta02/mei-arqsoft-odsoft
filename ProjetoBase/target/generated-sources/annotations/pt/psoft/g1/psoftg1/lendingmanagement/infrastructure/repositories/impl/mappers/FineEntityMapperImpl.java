package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-20T16:56:20+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class FineEntityMapperImpl implements FineEntityMapper {

    @Autowired
    private LendingEntityMapper lendingEntityMapper;

    @Override
    public Fine toModel(FineEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Lending lending = null;

        lending = lendingEntityMapper.toModel( entity.getLending() );

        Fine fine = new Fine( lending );

        return fine;
    }

    @Override
    public FineEntity toEntity(Fine model) {
        if ( model == null ) {
            return null;
        }

        LendingEntity lending = null;
        int fineValuePerDayInCents = 0;
        int centsValue = 0;

        lending = lendingEntityMapper.toEntity( model.getLending() );
        fineValuePerDayInCents = model.getFineValuePerDayInCents();
        centsValue = model.getCentsValue();

        FineEntity fineEntity = new FineEntity( fineValuePerDayInCents, centsValue, lending );

        return fineEntity;
    }
}
