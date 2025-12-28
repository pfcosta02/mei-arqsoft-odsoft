package pt.psoft.g1.psoftg1.featuremanagement.services;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FeatureService {
    private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

    private int rolloutPercentage = 0;
    private final Set<String> betaUsers = Set.of("admin@gmail.com");
    private boolean killSwitch = false;

    // DARK LAUNCH: Feature executa mas não mostra ao user
    private boolean darkLaunchMode = false;
    private final ConcurrentHashMap<String, AtomicLong> darkLaunchMetrics = new ConcurrentHashMap<>();

    public boolean isFeatureEnabledForUser(String userId)
    {
        if (killSwitch) return false; // feature disabled for all
        if (betaUsers.contains(userId)) return true;

        int hash = Math.abs(userId.hashCode() % 100);
        return hash < rolloutPercentage;
    }

    /**
     * DARK LAUNCH: Executa feature invisível ao user
     * - Código executa em produção
     * - Métricas são coletadas
     * - User NÃO vê resultado
     * - Permite validar feature antes de release completo
     *
     * @param userId User ID
     * @param featureName Nome da feature
     * @return true se feature deve executar (mas resultado não é mostrado)
     */
    public boolean shouldExecuteInDarkLaunch(String userId, String featureName)
    {
        if (!darkLaunchMode) {
            return false; // Dark launch desativado
        }

        // Feature executa para todos em dark launch
        boolean shouldExecute = isFeatureEnabledForUser(userId);

        if (shouldExecute)
        {
            // Incrementa métricas
            darkLaunchMetrics.computeIfAbsent(featureName, k -> new AtomicLong(0)).incrementAndGet();

            logger.info("[DARK LAUNCH] Feature '{}' executed for user '{}' (invisible to user)",
                    featureName, userId);
        }

        return shouldExecute;
    }

    /**
     * Retorna métricas coletadas durante Dark Launch
     */
    public ConcurrentHashMap<String, AtomicLong> getDarkLaunchMetrics()
    {
        return darkLaunchMetrics;
    }

    /**
     * Reseta métricas de Dark Launch
     */
    public void resetDarkLaunchMetrics()
    {
        darkLaunchMetrics.clear();
        logger.info("[DARK LAUNCH] Metrics reset");
    }

    public void setRolloutPercentage(int rolloutPercentage)
    {
        this.rolloutPercentage = rolloutPercentage;
        logger.info("Rollout percentage set to {}%", rolloutPercentage);
    }

    // Admin can activate/deactivate kill switch
    public void setKillSwitch(boolean state)
    {
        this.killSwitch = state;
        logger.warn("Kill switch {}", state ? "ACTIVATED" : "DEACTIVATED");
    }

    // Admin can activate/deactivate dark launch mode
    public void setDarkLaunchMode(boolean state)
    {
        this.darkLaunchMode = state;
        logger.info("Dark Launch mode {}", state ? "ACTIVATED" : "DEACTIVATED");
        if (!state) {
            resetDarkLaunchMetrics();
        }
    }

    public boolean isDarkLaunchMode()
    {
        return darkLaunchMode;
    }
}