package otamusan.nec.block.blockstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import net.minecraft.state.IProperty;
import otamusan.nec.common.Lib;

/*
 * Now not using
 */
public class PropertySideRender implements IProperty<SideRender> {

	@Override
	public String getName() {
		return Lib.BSPROPERTY_SIDERENDER;
	}

	@Override
	public Collection<SideRender> getAllowedValues() {
		return new ArrayList<SideRender>() {
			{
				add(new SideRender(true));
				add(new SideRender(false));
			}
		};
	}

	@Override
	public Class<SideRender> getValueClass() {
		return SideRender.class;
	}

	@Override
	public Optional<SideRender> parseValue(String var1) {
		return Optional.of(new SideRender(var1.length() == 17));
	}

	@Override
	public String getName(SideRender var1) {
		return "issiderender_" + var1.isrenderside;
	}

}
