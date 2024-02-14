package xueluoanping.fluiddrawerslegacy.client.render;

public class FluidAnimation {
    private int lastFluidAmount = 0;
    private int cacheFluidAmount = 0;
    private double lastAnimationTime = 0d;
    private boolean cutStartAnimation = false;

    public void setCutStartAnimation(boolean cutStartAnimation) {
        this.cutStartAnimation = cutStartAnimation;
    }

    public int getAndUpdateLastFluidAmount( int expectFluidAmount,double animationTime) {
        // int expectFluidAmount = this.groupData.tank.getFluidAmount();
        if (expectFluidAmount != this.lastFluidAmount) {
            int fluidAmountChange = (expectFluidAmount - this.lastFluidAmount);
            boolean isFluidUpdate = expectFluidAmount != cacheFluidAmount;
            boolean hasEnoughFluidAmount = Math.abs(fluidAmountChange) > 200;
            boolean isTooQuickAnimation = isFluidUpdate && animationTime - this.lastAnimationTime < 3;
            // FluidDrawersLegacyMod.logger(lastFluidAmount+""+isFluidUpdate+"Fluid Update,"+isTooQuickAnimation+ "" + "," + this.lastAnimationTime);
            boolean shouldAnimation = hasEnoughFluidAmount && !isTooQuickAnimation && !cutStartAnimation;
            if (shouldAnimation) {
                // this.lastFluidAmount += fluidAmountChange > 0 ? 50 : -50;
                this.lastFluidAmount += fluidAmountChange * 0.125f;
            } else {
                this.lastFluidAmount = expectFluidAmount;
            }
            if (isFluidUpdate) {
                this.lastAnimationTime = animationTime;
            }
            cutStartAnimation = false;
        }
        this.cacheFluidAmount = expectFluidAmount;
        return lastFluidAmount;
    }
}
