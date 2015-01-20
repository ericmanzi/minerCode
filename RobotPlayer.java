package dummyplayer;

import battlecode.client.viewer.AbstractDrawObject.RobotInfo;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

/**
 * Robot player that does nothing (for testing purposes).
 */
public class RobotPlayer {
	static int numMinerFactories=0;
	static int numBeavers=0;
	static RobotController rc;
	static int myRange;
	static Team enemyTeam;  
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH, Direction.SOUTH_WEST};


	public static void run(RobotController tomato) throws GameActionException {
		rc= tomato;
		myRange = rc.getType().attackRadiusSquared;
		while (true) {
			int randy = (int)(Math.random()*10);
			Direction facing = directions[(int)(Math.random()*10)];
			if (rc.getType() == RobotType.HQ) {
				try {
					if (rc.isWeaponReady()) {
						attackSomething();
						if (rc.canSpawn(facing, RobotType.BEAVER) && numBeavers<2) {
							rc.spawn(facing, RobotType.BEAVER);
							numBeavers++;
						}
					}
				} catch (Exception e) {
					System.out.println("Tower Exception");
					e.printStackTrace();
				}
			}
			if (rc.getType() == RobotType.TOWER) {
				try {
					if (rc.isCoreReady() && rc.isWeaponReady()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("Tower Exception");
					e.printStackTrace();
				}
			}
			if (rc.getType() == RobotType.BEAVER) {
				try {
					if (rc.isCoreReady()) {
						if (Math.random()<0.3 && rc.canMove(directions[randy])) {
							rc.move(directions[randy]);;
						} else if (Math.random()<0.6){
							rc.mine();
						} else {
							
							if (rc.getTeamOre() > RobotType.MINERFACTORY.oreCost && numMinerFactories<3 && 
									rc.canBuild(directions[randy], RobotType.MINERFACTORY)) {
								rc.build(directions[randy], RobotType.MINERFACTORY);
								numMinerFactories ++;
								System.out.println("MINER FACTORIES: "+numMinerFactories);
							}
						}
					}
				} catch (Exception e) {
					System.out.println("BEAVER Exception");
					e.printStackTrace();
				}
			}
			if (rc.getType() == RobotType.MINERFACTORY) {
				if (rc.isCoreReady() && rc.canSpawn(directions[randy], RobotType.MINER)) {
					rc.spawn(directions[randy], RobotType.MINER);
				}
			}
			
			if (rc.getType() == RobotType.MINER) {
				if (rc.isCoreReady()) {
					if (!minerMove()) mineHere();
				}
			}
		}
	}

	
	
	private static boolean minerMove() throws GameActionException {
		MapLocation myLoc = rc.getLocation();
		MapLocation[] surroundingLocs = myLoc.getAllMapLocationsWithinRadiusSq(myLoc, 4);
		double myOre = rc.senseOre(myLoc);
		if (rc.senseOre(myLoc)>10) {
			return false;
		} else {
			for (MapLocation loc: surroundingLocs) {
				Direction d = myLoc.directionTo(loc);
				if (rc.senseOre(loc)>myOre && !rc.isLocationOccupied(loc) && rc.canMove(d)) {
					rc.move(d);
					return true;
				}
			}
			return false;
		}
	}
	
	static void attackSomething() throws GameActionException {
		battlecode.common.RobotInfo[] enemies = rc.senseNearbyRobots(myRange,
				enemyTeam);
		if (enemies.length > 0) {
			rc.attackLocation(enemies[0].location);
		}
	}
	
	private static void mineHere() throws GameActionException {
		if (rc.senseOre(rc.getLocation())>0 && rc.canMine()) rc.mine();
	}
	
	
	
	
}
