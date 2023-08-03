package us.timinc.mc.cobblemon.koshiny.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.timinc.mc.cobblemon.koshiny.KoShiny;

@Mixin(value = PokemonSpawnAction.class, remap = false)
public abstract class PokemonSpawnActionMixin {
    @Shadow
    private PokemonProperties props;

    @Inject(method = "createEntity()Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;", at = @At("HEAD"))
    private void modifyShinyRate(CallbackInfoReturnable<Entity> cir) {
        SpawningContext ctx = ((PokemonSpawnAction) (Object) this).getCtx();
        KoShiny.INSTANCE.modifyShinyRate(ctx, props);
    }
}
