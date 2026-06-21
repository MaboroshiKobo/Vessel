package org.maboroshi.vessel.config.objects.effects;

import de.exlll.configlib.Configuration;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.sound.Sound;

@Configuration
public class EffectGroup {
    public Map<String, Sound> sounds = new HashMap<>();
    public Map<String, ParticleEffect> particles = new HashMap<>();

    public EffectGroup() {}

    public EffectGroup(Map<String, Sound> sounds, Map<String, ParticleEffect> particles) {
        if (sounds != null) this.sounds = sounds;
        if (particles != null) this.particles = particles;
    }
}
