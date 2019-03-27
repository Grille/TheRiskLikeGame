import java.util.Random;

import gne.Node;
import gne.Texture;
import gne.World;

public class Util {
  private Util() {}
  
	static public World initWorld() {

		Texture town,mount,ground;
		
    	town = new Texture("file:/../data/png/town.png");	

		Random rnd = new Random();
		World world = new World(3200,2000);
	
		Node[] na,sa,eu,af,as,au;
		na = new Node[]{
			new Node(100*2,150*2,"Alaska"),//0
			new Node(250*2,140*2,"Northwest Territory"),
			new Node(500*2,50*2,"Greenland"),
			new Node(200*2,250*2,"Alberta"),
			new Node(330*2,255*2,"Ontario"),
			new Node(470*2,280*2,"Quebee"),
			new Node(240*2,380*2,"Western States"),
			new Node(370*2,390*2,"Eastern States"),
			new Node(310*2,520*2,"Central America"),
		};
		sa = new Node[]{
			new Node(850,1220,"Venezuela"),//9
			new Node(880,1480,"Peru"),//10
			new Node(1060,1420,"Brazil"),
			new Node(910,1710,"Argentina")
		};
		eu = new Node[]{
			new Node(1180,300,"Iceland"),//13
			new Node(1320,450,"Great Britain"),
			new Node(1680,200,"Scandinavia"),
			new Node(1340,730,"Western Europe"),//16
			new Node(1610,504,"Northern Europe"),
			new Node(1680,630,"Southern Europe"),
			new Node(1980,420,"Ukraine")		
		};
		af = new Node[]{
			new Node(1440,1120,"North Africa"),//20
			new Node(1670,1000,"Egypt"),
			new Node(1690,1360,"Congo"),
			new Node(1880,1230,"East Africa"),//23
			new Node(1740,1670,"South Africa"),
			new Node(2000,1630,"Madagascar")
		};
		as = new Node[]{
			new Node(2270,340,"Ural"),//26
			new Node(2520,330,"Siberia"),
			new Node(2730,170,"Yakutsk"),//28
			new Node(2740,420,"Irkutsk"),
			new Node(2940,440,"Kamchatka"),//30
			new Node(2240,680,"Afghanistan"),
			new Node(2570,810,"China"),//32
			new Node(2780,590,"Mongolia"),
			new Node(3060,720,"Japan"),
			new Node(1980,920,"Middle East"),//35
			new Node(2340,1040,"India"),
			new Node(2600,1070,"Siam")//37
		};
		au = new Node[]{	
			new Node(2730,1360,"Indonesia"),//38
			new Node(3050,1380,"New Guinea"),
			new Node(2800,1700,"Western Australia"),//40
			new Node(3040,1740,"Eastern Australia")
		};
		
		na[0].conectWithNodes(new Node[] {na[1],na[3],na[0],as[4]});
		na[1].conectWithNodes(new Node[] {na[2],na[3],na[4]});
		na[2].conectWithNodes(new Node[] {na[5],eu[0]});
		na[3].conectWithNodes(new Node[] {na[4],na[6]});
		na[4].conectWithNodes(new Node[] {na[5],na[6],na[7]});
		na[5].conectWithNodes(new Node[] {na[7]});
		na[6].conectWithNodes(new Node[] {na[7],na[8]});
		na[7].conectWithNodes(new Node[] {na[8]});
		na[8].conectWithNodes(new Node[] {sa[0]});
		
		sa[0].conectWithNodes(new Node[] {sa[1],sa[2]});
		sa[1].conectWithNodes(new Node[] {sa[2],sa[3]});
		sa[2].conectWithNodes(new Node[] {sa[3],af[0]});
			
		eu[0].conectWithNodes(new Node[] {eu[1]});
		eu[1].conectWithNodes(new Node[] {eu[3],eu[4]});
		eu[2].conectWithNodes(new Node[] {eu[4],eu[6]});
		eu[3].conectWithNodes(new Node[] {eu[5],af[0]});
		eu[4].conectWithNodes(new Node[] {eu[5],eu[6]});
		eu[5].conectWithNodes(new Node[] {eu[6],af[0],af[1],as[9]});
		eu[6].conectWithNodes(new Node[] {as[0],as[5],as[9]});
		
		af[0].conectWithNodes(new Node[] {af[1],af[2],af[3]});
		af[1].conectWithNodes(new Node[] {af[3],as[9]});
		af[2].conectWithNodes(new Node[] {af[3],af[4]});
		af[3].conectWithNodes(new Node[] {af[4],af[5],as[9]});
		af[4].conectWithNodes(new Node[] {af[5]});
		
		as[0].conectWithNodes(new Node[] {as[1],as[5],as[6]});
		as[1].conectWithNodes(new Node[] {as[2],as[3],as[6],as[7]});
		as[2].conectWithNodes(new Node[] {as[3],as[4]});
		as[3].conectWithNodes(new Node[] {as[4],as[7]});
		as[4].conectWithNodes(new Node[] {as[7],as[8]});
		as[5].conectWithNodes(new Node[] {as[6],as[9],as[10]});
		as[6].conectWithNodes(new Node[] {as[7],as[10],as[11]});
		as[7].conectWithNodes(new Node[] {as[8]});
		as[9].conectWithNodes(new Node[] {as[10]});
		as[10].conectWithNodes(new Node[] {as[11]});
		as[11].conectWithNodes(new Node[] {au[0]});
		
		au[0].conectWithNodes(new Node[] {au[1],au[2]});
		au[1].conectWithNodes(new Node[] {au[2],au[3]});
		au[2].conectWithNodes(new Node[] {au[3]});

		world.addNodes(na);
		world.addNodes(sa);
		world.addNodes(eu);
		world.addNodes(af);
		world.addNodes(as);
		world.addNodes(au);

		world.groupNodes("North America", "#b4c62e",5, na);
		world.groupNodes("South America", "#b24a32",2, sa);
		world.groupNodes("Africa", "#554fa8",3, af);
		world.groupNodes("Europe", "#a47e3b",5, eu);
		world.groupNodes("Asia", "#418347",7, as);
		world.groupNodes("Australia", "#974070",2, au);
		
		world.setBackgroundGraphic(new Texture("file:/../data/png/ground.png"));
		world.setWaterGraphic(new Texture("file:/../data/png/water.png"));
		world.repeatX = true;
		world.save("world.nwf");
    	 	
		world.load("world.nwf");
		return world;
	}
}
