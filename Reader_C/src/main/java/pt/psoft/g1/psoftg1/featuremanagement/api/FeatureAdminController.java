package pt.psoft.g1.psoftg1.featuremanagement.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.psoft.g1.psoftg1.featuremanagement.services.FeatureService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/feature")
@PreAuthorize("hasRole('ADMIN')")
public class FeatureAdminController {
    private final FeatureService featureService;

    public FeatureAdminController(FeatureService featureService)
    {
        this.featureService = featureService;
    }

    @PostMapping("/killSwitch/activate")
    public String activateKillSwitch()
    {
        featureService.setKillSwitch(true);
        return "Kill switch activated!";
    }

    @PostMapping("/killSwitch/deactivate")
    public String deactivateKillSwitch()
    {
        featureService.setKillSwitch(false);
        return "Kill switch deactivated!";
    }

    @PostMapping("/rollout")
    public String setRollout(@RequestParam int percentage)
    {
        featureService.setRolloutPercentage(percentage);
        return "Rollout percentage updated to " + percentage + "%";
    }

    // ==========================================
    // DARK LAUNCH ENDPOINTS
    // ==========================================

    @PostMapping("/darkLaunch/activate")
    public String activateDarkLaunch()
    {
        featureService.setDarkLaunchMode(true);
        return "Dark Launch mode activated! Features will execute invisibly and collect metrics.";
    }

    @PostMapping("/darkLaunch/deactivate")
    public String deactivateDarkLaunch()
    {
        featureService.setDarkLaunchMode(false);
        return "Dark Launch mode deactivated! Metrics cleared.";
    }

    @GetMapping("/darkLaunch/metrics")
    public Map<String, Long> getDarkLaunchMetrics()
    {
        Map<String, Long> metrics = new HashMap<>();
        featureService.getDarkLaunchMetrics().forEach((key, value) ->
                metrics.put(key, value.get())
        );
        return metrics;
    }

    @PostMapping("/darkLaunch/metrics/reset")
    public String resetDarkLaunchMetrics()
    {
        featureService.resetDarkLaunchMetrics();
        return "Dark Launch metrics reset successfully.";
    }
}
