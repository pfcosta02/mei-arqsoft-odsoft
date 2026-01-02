package pt.psoft.g1.psoftg1.lendingmanagement.services;

import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingQueryDTO;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;

import java.util.List;

public interface LendingService {

        // ========== OPERAÇÕES DE LEITURA ==========

        /**
         * Retorna todos os empréstimos
         */
        List<LendingQueryDTO> getAllLendings();

        /**
         * Retorna um empréstimo por ID
         */
        LendingQueryDTO getLendingById(Long id);
//
//        /**
//         * Retorna todos os empréstimos de um leitor
//         */
//        List<LendingQueryDTO> getLendingsByReader(Long readerId);

        /**
         * Retorna apenas os empréstimos ativos (não devolvidos) de um leitor
         */
        List<LendingQueryDTO> getActiveLendingsByReader(Long readerId);

        /**
         * Retorna empréstimos atrasados
         */
        List<LendingQueryDTO> getOverdueLendings();

        /**
         * Conta empréstimos do ano atual
         */
        int countLendingsCurrentYear();

        /**
         * Retorna duração média dos empréstimos
         */
        Double getAverageLendingDuration();

        // ========== OPERAÇÕES DE SINCRONIZAÇÃO (Eventos do RabbitMQ) ==========

        /**
         * Sincroniza um empréstimo criado
         */
        void createFromEvent(LendingEventAMQP event);

        /**
         * Sincroniza um empréstimo atualizado
         */
        void updateFromEvent(LendingEventAMQP event);

        /**
         * Sincroniza um empréstimo eliminado
         */
        void deleteFromEvent(LendingEventAMQP event);
    }
