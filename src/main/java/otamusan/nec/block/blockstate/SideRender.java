package otamusan.nec.block.blockstate;

/*
 * Now not using
 */
public class SideRender implements Comparable<SideRender> {
	public boolean isrenderside;

	//Maybe add details to deal with direction
	public SideRender(boolean isrenderside) {
		this.isrenderside = isrenderside;
	}

	@Override
	public int compareTo(SideRender o) {
		if (o.isrenderside = isrenderside) {
			return 0;
		} else if (o.isrenderside && !isrenderside) {
			return 1;
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		return ((SideRender) obj).isrenderside == isrenderside;
	}
}
