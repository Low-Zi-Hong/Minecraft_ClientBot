package net.wtf.AutoRun;

import net.ClientInitialiser;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.wtf.PathFinding.Astar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Wocao {

    private static boolean run = false;
    public static int indicator = 0;

    private static List<PathUnit> pathUnitList = new ArrayList<>();
    public static Queue<OperationUnit> operationQueue = new LinkedList<>();

    //Callback
    private static Callback callback_wocao = success -> {
        if (success) {
        System.out.println("Operation succeeded!");
        ExecuteNextOperation();
    } else {
        System.out.println("Operation failed.");
    }};


    private static void OnInitiate() {
        if (run) return; // Prevent multiple starts
        run = true;
        pathUnitList = ClientInitialiser.blocKPosToRender.reversed();
        indicator = 0;
    }

    public static void GenerateOperationList() {
        //check if the pathUnitList is not empty
        if (pathUnitList.size() <= 0) pathUnitList = ClientInitialiser.blocKPosToRender;

        //illiterate throught the list
        for (PathUnit unit : pathUnitList) {

            ClientWorld world = MinecraftClient.getInstance().world;

            BlockPos block_0_pos = new BlockPos(unit.targetPos).add(0,-1,0);
            BlockPos block_1_pos = new BlockPos(unit.targetPos);
            BlockPos block_2_pos = new BlockPos(unit.targetPos).add(0,1,0);
            BlockPos block_3_pos = new BlockPos(unit.targetPos).add(0,2,0);
            BlockPos block_4_pos = new BlockPos(unit.targetPos).add(0,3,0);

            BlockState block_0 = world.getBlockState(block_0_pos);
            BlockState block_1 = world.getBlockState(block_1_pos);
            BlockState block_2 = world.getBlockState(block_2_pos);
            BlockState block_3 = world.getBlockState(block_3_pos);
            BlockState block_4 = world.getBlockState(block_4_pos);

            switch (unit.dirFromPrevious) {
                case Up -> {
                    System.out.println("A Up Operation is added");

                    if(!block_2.isOf(Blocks.AIR) && !block_2.isOf(Blocks.SNOW)){
                        operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));
                    }
                    if (block_0.isOf(Blocks.AIR) && !block_0.isOf(Blocks.SNOW)){
                        operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.JumpnPlace,callback_wocao));
                    }
                    // Add your logic for the "Up" direction here
                }
                case Down -> {
                    System.out.println("A Down Operation is added");

                    if(!block_1.isOf(Blocks.AIR)&& !block_1.isOf(Blocks.SNOW) )operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Wait,callback_wocao));
                    // Add your logic for the "Down" direction here
                }
                case Forward ,Back, Left,Right -> {
                    System.out.println("A Forward Operation is added");

                    if(!block_1.isOf(Blocks.AIR)&& !block_1.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(!block_2.isOf(Blocks.AIR)&& !block_2.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));

                    if(block_0.isOf(Blocks.AIR)&& !block_0.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_0_pos, OperationEnum.Place,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Walk,callback_wocao));
                    // Add your logic for the "Forward" direction here
                }
                //case Back -> {
                //    System.out.println("A Back Operation is added");
                //    // Add your logic for the "Back" direction here
                //}
                //case Left -> {
                //    System.out.println("A Left Operation is added");
                //    // Add your logic for the "Left" direction here
                //}
                //case Right -> {
                //    System.out.println("A Right Operation is added");
                //    // Add your logic for the "Right" direction here
                //}
                case JumpUpForward -> {
                    System.out.println("A JumpUpForward Operation is added");

                    //check block above player
                    if(!world.getBlockState(block_2_pos.add(-1,0,0)).isOf(Blocks.AIR)&& !world.getBlockState(block_2_pos.add(-1,0,0)).isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos.add(-1,0,0),OperationEnum.Break,callback_wocao));

                    if(!block_0.isOf(Blocks.AIR) && !block_0.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.Place,callback_wocao));

                    if(!block_1.isOf(Blocks.AIR)&& !block_1.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(!block_2.isOf(Blocks.AIR)&& !block_2.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.WalknJump,callback_wocao));
                    // Add your logic for the "JumpUpForward" direction here
                }
                case JumpUpBack -> {
                    System.out.println("A JumpUpBack Operation is added");
                    // Add your logic for the "JumpUpBack" direction here
                    if(!world.getBlockState(block_2_pos.add(1,0,0)).isOf(Blocks.AIR) || !world.getBlockState(block_2_pos.add(1,0,0)).isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos.add(-1,0,0),OperationEnum.Break,callback_wocao));

                    if(!block_0.isOf(Blocks.AIR) && !block_0.isOf(Blocks.SNOW) ) operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.Place,callback_wocao));

                    if(!block_1.isOf(Blocks.AIR) && !block_1.isOf(Blocks.SNOW) ) operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(!block_2.isOf(Blocks.AIR) && !block_2.isOf(Blocks.SNOW) ) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.WalknJump,callback_wocao));
                }
                case JumpUpLeft -> {
                    System.out.println("A JumpUpLeft Operation is added");
                    // Add your logic for the "JumpUpLeft" direction here
                    if(!world.getBlockState(block_2_pos.add(0,0,1)).isOf(Blocks.AIR)) operationQueue.add(new OperationUnit(block_2_pos.add(-1,0,0),OperationEnum.Break,callback_wocao));

                    if(block_0.isOf(Blocks.AIR)  && !block_0.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.Place,callback_wocao));

                    if(!block_1.isOf(Blocks.AIR) && !block_1.isOf(Blocks.SNOW) )operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(!block_2.isOf(Blocks.AIR) && !block_2.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.WalknJump,callback_wocao));
                }
                case JumpUpRight -> {
                    System.out.println("A JumpUpRight Operation is added");
                    // Add your logic for the "JumpUpRight" direction here
                    if(!world.getBlockState(block_2_pos.add(0,0,-1)).isOf(Blocks.AIR)) operationQueue.add(new OperationUnit(block_2_pos.add(-1,0,0),OperationEnum.Break,callback_wocao));

                    if(block_0.isOf(Blocks.AIR) && !block_0.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.Place,callback_wocao));

                    if(!block_1.isOf(Blocks.AIR)&& !block_1.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(!block_2.isOf(Blocks.AIR)&& !block_2.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.WalknJump,callback_wocao));
                }
                case JumpDownForward,JumpDownBack,JumpDownLeft,JumpDownRight -> {
                    System.out.println("A JumpDownForward Operation is added");
                    // Add your logic for the "JumpDownForward" direction here
                    if(!block_3.isOf(Blocks.AIR) && !block_3.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_3_pos,OperationEnum.Break,callback_wocao));
                    if(!block_2.isOf(Blocks.AIR)&& !block_2.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_2_pos,OperationEnum.Break,callback_wocao));
                    if(!block_1.isOf(Blocks.AIR)&& !block_1.isOf(Blocks.SNOW)) operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Break,callback_wocao));

                    if(block_0.isOf(Blocks.AIR)) operationQueue.add(new OperationUnit(block_0_pos,OperationEnum.Place,callback_wocao));

                    operationQueue.add(new OperationUnit(block_1_pos,OperationEnum.Walk,callback_wocao));
                }
                //case JumpDownBack -> {
                //    System.out.println("A JumpDownBack Operation is added");
                //    // Add your logic for the "JumpDownBack" direction here
                //}
                //case JumpDownLeft -> {
                //    System.out.println("A JumpDownLeft Operation is added");
                //    // Add your logic for the "JumpDownLeft" direction here
                //}
                //case JumpDownRight -> {
                //    System.out.println("A JumpDownRight Operation is added");
                //    // Add your logic for the "JumpDownRight" direction here
                //}
                default -> {
                    System.out.println("No direction matched");
                    // Add your logic for unmatched directions here
                }
            }

        }

        System.out.println("Operation Queue:");
        for (OperationUnit operation : operationQueue) {
            System.out.println("Operation: " + operation.Operation + " at " + operation.OperationPos);
        }
    }

    //Now is the real operation which runs
    private static boolean isRunning = false;
    private static int currentOperationIndex = 0;
    public static boolean Run = false;
    public static void StartOperation() {

        if (isRunning) return;
        operationQueue.clear();
        GenerateOperationList();
        isRunning = true;
        currentOperationIndex = 0;

        ExecuteNextOperation();


    }

    private static void ExecuteNextOperation()
    {
        if (currentOperationIndex >= operationQueue.size()) {
            isRunning = false; // Stop execution when all operations are done
            System.out.println("All operations completed.");
            return;
        }

        OperationUnit currentOperation = operationQueue.poll();
        System.out.println("Processing operation: " + currentOperation.Operation + " at " + currentOperation.OperationPos);

        currentOperation.start();


    }



    public static class PathUnit {
        public BlockPos targetPos;
        public Astar.Direction dirFromPrevious;

        public PathUnit(BlockPos _targetPos, Astar.Direction _dir) {
            targetPos = _targetPos;
            dirFromPrevious = _dir;
        }

        public PathUnit(int x, int y, int z, Astar.Direction _dir) {
            targetPos = new BlockPos(x, y, z);
            dirFromPrevious = _dir;
        }
    }

    public enum OperationEnum
    {
        Wait,
        Walk,
        WalknJump,
        Place,
        JumpnPlace,
        Break
    }

    public static class OperationUnit{
        public BlockPos OperationPos;
        public OperationEnum Operation;

        private Runnable task;
        private Callback callback;

        OperationUnit(BlockPos pos, OperationEnum operation, Callback _callback)
        {
            this.OperationPos = pos;
            this.Operation = operation;
            this.callback = _callback;

            switch (Operation)
            {
                case Wait -> task = () -> WaitToReact.waitAwhile(callback);
                case Walk -> task = () -> Walking.WalkToBlock(pos,false,callback);
                case WalknJump -> task = () -> Walking.WalkToBlock(pos, true,callback);
                case Break -> task = () -> BreakBlock.setBreakBlock(pos,callback);
                case Place -> task = () -> PlaceBlock.PlaceBlock(pos,callback);
                case JumpnPlace -> task = () -> JumpnPut.JumpAndPutBlock(pos, callback);
                default -> task = () -> System.out.println("No operation call");
            }


        }

        public void start(){task.run();}

        public Callback getCallback() {return this.callback;}

    }

    // Callback interface for feedback
    public interface Callback {
        void onComplete(boolean success);
    }


}