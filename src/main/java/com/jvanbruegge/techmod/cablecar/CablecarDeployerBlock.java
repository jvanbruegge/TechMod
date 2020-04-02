package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechModBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;

public class CablecarDeployerBlock extends TechModBlock {
    public CablecarDeployerBlock() {
        super(Properties.create(Material.PISTON), "cablecar_deployer", ItemGroup.TRANSPORTATION);
    }
}
