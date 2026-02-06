package net.wtf.PathFinding;

import net.ClientInitialiser;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.wtf.AutoRun.Wocao;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Astar {

    public static void Start() {
        if (ClientInitialiser.startPos != null && ClientInitialiser.endPos != null) {

            Thread AstarAlgorithm = new Thread( () -> {

                assert MinecraftClient.getInstance().player != null;
                BlockPos playerCurrentPos = MinecraftClient.getInstance().player.getBlockPos();

                BlockPos endPos = ClientInitialiser.endPos;

                //determine temperory End posistion
                //move the whole coordinate system and make the player current pos as the origin
                BlockPos endPosToPlayer = endPos.subtract(playerCurrentPos); //end pos relative to player pos

                if ( endPosToPlayer.getX() < 24 && endPosToPlayer.getX() > -24 && endPosToPlayer.getZ() < 24 && endPosToPlayer.getZ() > -24)
                {
                    try {
                        AstarAlgorithm(playerCurrentPos,endPos,true);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ClientInitialiser.runingWtfAlgo = false;
                }
                else
                {
                    Vec2f TempEnd2dPos = detTempEndPos(endPosToPlayer); //realative to player coor
                    Vec2f TempEndWorldPos = new Vec2f(playerCurrentPos.getX() + TempEnd2dPos.x, playerCurrentPos.getZ() + TempEnd2dPos.y);
                    BlockPos TempEndWorldBlockPos = new BlockPos((int)TempEndWorldPos.x, MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int)TempEndWorldPos.x, (int)TempEndWorldPos.y), (int)TempEndWorldPos.y);

                    try {
                        AstarAlgorithm(playerCurrentPos, TempEndWorldBlockPos,true);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //MinecraftClient.getInstance().player.sendMessage(Text.literal("temp end pos" + TempEndWorldBlockPos.toShortString()),false);

                    ClientInitialiser.tempEndPos = TempEndWorldBlockPos;
                    ClientInitialiser.runingWtfAlgo = true;
                }
            });

            AstarAlgorithm.start();


        }
    }

    private static Vec2f detTempEndPos(BlockPos endPosToPlayer)
    {

        //determine which direction player is going to go
        int x = endPosToPlayer.getX();
        int y = endPosToPlayer.getZ();//here use y to represent z is just for my convenient
        Vec2f resultCoor = null;
        // Handle edge cases where x or y is zero
        if (x == 0) {
            resultCoor = new Vec2f(0, y > 0 ? 24 : -24);
        } else if (y == 0) {
            resultCoor = new Vec2f(x > 0 ? 24 : -24, 0);
        } else {
            if (y < x && y >= -x) { // x = 24
                resultCoor = new Vec2f(24, calCoor(24, x, y));
            } else if (y > x && y <= -x) { // x = -24
                resultCoor = new Vec2f(-24, calCoor(-24, x, y));
            } else if (y >= x && y > -x) { // y = 24
                resultCoor = new Vec2f(calCoor(24, y, x), 24);
            } else if (y < x && y < -x) { // y = -24
                resultCoor = new Vec2f(calCoor(-24, y, x), -24);
            }
        }
        return resultCoor;

    }

    private static int calCoor(int variable, int x, int y) {
        if (x == 0) {
            throw new IllegalArgumentException("Division by zero: x is zero.");
        }
        return (int) ((double) y / x * variable);
    }

    //main Algorithm
    public enum Direction
    {
        Up{
            @Override
            public BlockPos Step(BlockPos ori)            {
                return ori.add(0,1,0);
            }

            public Direction Opposite(){
                return Down;
            }
        },
        Down {
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,-1,0);
            }
            public Direction Opposite(){
                return Up;
            }
        },
        Forward{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(1,0,0);
            }
            public Direction Opposite(){
                return Back;
            }
        },
        Back{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(-1,0,0);
            }
            public Direction Opposite(){
                return Forward;
            }
        },
        Left{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,0,-1);
            }
            public Direction Opposite(){
                return Right;
            }
        },
        Right{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,0,1);
            }
            public Direction Opposite(){
                return Left;
            }
        },
        JumpUpForward{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(1,1,0);
            }
            public Direction Opposite(){
                return JumpDownBack;
            }
        },
        JumpUpBack{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(-1,1,0);
            }
            public Direction Opposite(){
                return JumpDownForward;
            }
        },
        JumpUpLeft{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,1,-1);
            }
            public Direction Opposite(){
                return JumpDownRight;
            }
        },
        JumpUpRight{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,1,1);
            }
            public Direction Opposite(){
                return JumpDownLeft;
            }
        },
        JumpDownForward{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(1,-1,0);
            }
            public Direction Opposite(){
                return JumpUpBack;
            }
        },
        JumpDownBack{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(-1,-1,0);
            }
            public Direction Opposite(){
                return JumpUpForward;
            }
        },
        JumpDownLeft{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,-1,-1);
            }
            public Direction Opposite(){
                return JumpUpRight;
            }
        },
        JumpDownRight{
            @Override
            public BlockPos Step(BlockPos ori) {
                return ori.add(0,-1,1);
            }
            public Direction Opposite(){
                return JumpUpLeft;
            }
        };

        public abstract BlockPos Step(BlockPos ori);
        public abstract Direction Opposite();
    }

    static class PathUnit
    {
        public int step_id;

        BlockPos PathUnit_Coor;

        public int previous_step_id;
        public Direction previous_Direction;

        public float G_cost; //how far away from starting node
        public float H_cost; //how far away to end node
        public float F_cost; //G_cost + H_cost

        PathUnit (int _step_id, BlockPos Start_Coor, BlockPos End_Coor) {
            step_id = _step_id;

            PathUnit_Coor = Start_Coor;
            previous_step_id = 0;
            previous_Direction = Direction.Forward;

            G_cost = 0;
            H_cost = heuristic(PathUnit_Coor.getX(), PathUnit_Coor.getY(), PathUnit_Coor.getZ(), End_Coor.getX(), End_Coor.getY(), End_Coor.getZ());
            F_cost = G_cost + H_cost;

        }
        PathUnit(int _step_id, PathUnit PreviousPath, Direction Direction, BlockPos End_Coor) {
            step_id = _step_id;
            previous_step_id = PreviousPath.step_id;
            previous_Direction = Direction;

            switch (Direction)
            {
                case Up ->              PathUnit_Coor = Astar.Direction.Up.Step(PreviousPath.PathUnit_Coor);
                case Down ->            PathUnit_Coor = Astar.Direction.Down.Step(PreviousPath.PathUnit_Coor);
                case Forward ->         PathUnit_Coor = Astar.Direction.Forward.Step(PreviousPath.PathUnit_Coor);
                case Back ->            PathUnit_Coor = Astar.Direction.Back.Step(PreviousPath.PathUnit_Coor);
                case Left ->            PathUnit_Coor = Astar.Direction.Left.Step(PreviousPath.PathUnit_Coor);
                case Right ->           PathUnit_Coor = Astar.Direction.Right.Step(PreviousPath.PathUnit_Coor);
                case JumpUpForward ->   PathUnit_Coor = Astar.Direction.JumpUpForward.Step(PreviousPath.PathUnit_Coor);
                case JumpUpBack ->      PathUnit_Coor = Astar.Direction.JumpUpBack.Step(PreviousPath.PathUnit_Coor);
                case JumpUpLeft ->      PathUnit_Coor = Astar.Direction.JumpUpLeft.Step(PreviousPath.PathUnit_Coor);
                case JumpUpRight ->     PathUnit_Coor = Astar.Direction.JumpUpRight.Step(PreviousPath.PathUnit_Coor);
                case JumpDownForward -> PathUnit_Coor = Astar.Direction.JumpDownForward.Step(PreviousPath.PathUnit_Coor);
                case JumpDownBack ->    PathUnit_Coor = Astar.Direction.JumpDownBack.Step(PreviousPath.PathUnit_Coor);
                case JumpDownLeft ->    PathUnit_Coor = Astar.Direction.JumpDownLeft.Step(PreviousPath.PathUnit_Coor);
                case JumpDownRight ->   PathUnit_Coor = Astar.Direction.JumpDownRight.Step(PreviousPath.PathUnit_Coor);

            }

            G_cost = PreviousPath.G_cost + 1;
            H_cost = heuristic(PathUnit_Coor.getX(), PathUnit_Coor.getY(), PathUnit_Coor.getZ(), End_Coor.getX(), End_Coor.getY(), End_Coor.getZ());
            F_cost = G_cost + H_cost;

            ClientWorld world = MinecraftClient.getInstance().world;
            if(world.getBlockState(PathUnit_Coor.add(0,-1,0)).isOf(Blocks.AIR) || world.getBlockState(PathUnit_Coor.add(0,-1,0)).isOf(Blocks.SNOW) || world.getBlockState(PathUnit_Coor.add(0,-1,0)).isOf(Blocks.SHORT_GRASS))
            {
                F_cost += 50;
            }
            if(!world.getBlockState(PathUnit_Coor).isOf(Blocks.AIR) || !world.getBlockState(PathUnit_Coor.add(0,1,0)).isOf(Blocks.AIR))
            {
                 if(!world.getBlockState(PathUnit_Coor).isOf(Blocks.SNOW) && !world.getBlockState(PathUnit_Coor).isOf(Blocks.SHORT_GRASS))
                 {
                     F_cost += 30;
                 }
            }
        }
    }

    private static int heuristic(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = x1 - x2; // Difference in x
        int dy = y1 - y2; // Difference in y
        int dz = z1 - z2; // Difference in z

        // Custom heuristic: a^2 + b^2 + c^2
        return dx * dx + dy * dy + dz * dz;
    }

    public static void AstarAlgorithm(BlockPos start, BlockPos end, @Nullable Boolean preciseLocation) throws InterruptedException {
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("Algorithm star!" + end.toShortString()));
        List<PathUnit> active_array = new ArrayList<PathUnit>(0);
        List<PathUnit> unactive_array = new ArrayList<PathUnit>(0);

        boolean reachedEnd = false;

        int currentid = 0;
        int BestPathId = 0;

        //generate the path (main Algorithm)
        while (!reachedEnd && unactive_array.size() <= 100000000) {
            //context.getSource().getPlayer().sendMessage(Text.literal("??  " + reachedEnd));
            PathUnit Lowest_F_Cost_Path = null;
            float F_cost;
            if (active_array.isEmpty()) {
                active_array.add(new PathUnit(currentid, start, end));
                currentid += 1;

                Lowest_F_Cost_Path = active_array.getFirst();
                F_cost = Lowest_F_Cost_Path.F_cost;
            } else {

                //Find lowest F Cost

                Lowest_F_Cost_Path = active_array.getFirst();
                F_cost = Lowest_F_Cost_Path.F_cost;
                for (PathUnit i : active_array) {
                    if (i.F_cost <= F_cost) {
                        Lowest_F_Cost_Path = i;
                        F_cost = i.F_cost;
                    }
                }
            }

            //for each dir calculate the cost
            for (Direction dir : Direction.values())
            {
                if(Lowest_F_Cost_Path.previous_Direction != dir.Opposite() && checkBlockAvalaible(dir.Step(Lowest_F_Cost_Path.PathUnit_Coor))) {
                    PathUnit buffer= new PathUnit(currentid, Lowest_F_Cost_Path, dir, end);
                    active_array.add(buffer);

                    if(preciseLocation == null || preciseLocation == false)
                    {
                            if(checkReachEnd(buffer.PathUnit_Coor, end))
                            {
                                reachedEnd = true;
                                BestPathId = buffer.step_id;
                            }

                    }
                    else
                    {
                        if(buffer.PathUnit_Coor.equals(end))
                        {
                            reachedEnd = true;
                            BestPathId = buffer.step_id;
                        }
                    }
                    currentid++;
                }
            }

            unactive_array.add(Lowest_F_Cost_Path);
            active_array.remove(Lowest_F_Cost_Path);
        }


        if(!reachedEnd) {
            System.out.println("fail to search path");
            InterruptedException e = new InterruptedException("Fail to search path");
            throw e;
        }

        //trace back the real path logic

        List<PathUnit> bestPathTrace = new ArrayList<>();

        PathUnit addedPath = null;

        //trace back the coor
        for (PathUnit i : active_array)
        {
            if(i.step_id == BestPathId)
            {
                addedPath = i;
            }
        }

        bestPathTrace.add(addedPath);

        while(addedPath != null && !addedPath.PathUnit_Coor.equals(start)) {
            boolean found = false;
            for (PathUnit path : unactive_array) {
                if (path.step_id == addedPath.previous_step_id) {
                    addedPath = path;
                    bestPathTrace.add(addedPath);
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }

        for(int i = bestPathTrace.size() -1; i >= 0 ; i--)
        {
            ClientInitialiser.blocKPosToRender.add(new Wocao.PathUnit(bestPathTrace.get(i).PathUnit_Coor,bestPathTrace.get(i).previous_Direction));
        }

        //MinecraftClient.getInstance().player.sendMessage(Text.literal("Algorithm finish!"));

    }

    private static boolean checkBlockAvalaible(BlockPos posToCheck)
    {

        return true;
    }

    public static boolean checkReachEnd(BlockPos currentPos, BlockPos endPos)
    {
        //this one I ignore the Y axis so the player just arrive at the x,z coor and at any level of y axis
        int c_x = currentPos.getX();
        int c_y = currentPos.getY();
        int c_z = currentPos.getZ();

        int e_x1 = endPos.getX() - 5;
        int e_y1 = endPos.getY() - 5;
        int e_z1 = endPos.getZ() - 5;
        int e_x2 = endPos.getX() + 5;
        int e_y2 = endPos.getY() + 5;
        int e_z2 = endPos.getZ() + 5;

        return c_x > e_x1 && c_x < e_x2 && c_y > e_y1 && c_y < e_y2 &&  c_z > e_z1 && c_z < e_z2;

    }

}
