import java.util.Random;

import gne.Node;
import gne.Texture;
import gne.World;

public class Util {
  private Util() {}
  
	static public World initWorld() {
		
		
		Texture town,mount,ground;
		
		
    	town = new Texture("file:/../data/png/town/town.png");	
    	mount = new Texture("file:/../data/png/deco/mountain.png");	
    	ground = new Texture("file:/../data/png/deco/ground.png");	
    	
		Random rnd = new Random();
		World world = new World(3200,2000);
	
		Node[] nodes = new Node[]{
			new Node(100*2,150*2,town,"Alaska"),//0
			new Node(250*2,140*2,town,"Northwest Territory"),
			new Node(500*2,50*2,town,"Greenland"),
			new Node(200*2,250*2,town,"Alberta"),
			new Node(330*2,255*2,town,"Ontario"),
			new Node(470*2,280*2,town,"Quebee"),
			new Node(240*2,380*2,town,"Western States"),
			new Node(370*2,390*2,town,"Eastern States"),
			new Node(310*2,520*2,town,"Central America"),
			
			new Node(850,1220,town,"Venezuela"),//9
			new Node(880,1480,town,"Peru"),//10
			new Node(1060,1420,town,"Brazil"),
			new Node(910,1710,town,"Argentina"),
			
			new Node(1180,300,town,"Iceland"),//13
			new Node(1320,450,town,"Great Britain"),
			new Node(1680,200,town,"Scandinavia"),
			new Node(1340,730,town,"Western Europe"),//16
			new Node(1610,504,town,"Northern Europe"),
			new Node(1680,630,town,"Southern Europe"),
			new Node(1980,420,town,"Ukraine"),
			
			new Node(1440,1120,town,"North Africa"),//20
			new Node(1670,1000,town,"Egypt"),
			new Node(1690,1360,town,"Congo"),
			new Node(1880,1230,town,"East Africa"),//23
			new Node(1740,1670,town,"South Africa"),
			new Node(2000,1630,town,"Madagascar"),
			
			new Node(2270,340,town,"Ural"),//26
			new Node(2520,330,town,"Siberia"),
			new Node(2730,170,town,"Yakutsk"),//28
			new Node(2740,420,town,"Irkutsk"),
			new Node(2940,440,town,"Kamchatka"),//30
			new Node(2240,680,town,"Afghanistan"),
			new Node(2570,810,town,"China"),//32
			new Node(2780,590,town,"Mongolia"),
			new Node(3060,720,town,"Japan"),
			new Node(1980,920,town,"Middle East"),//35
			new Node(2340,1040,town,"India"),
			new Node(2600,1070,town,"Siam"),//37
			
			new Node(2730,1360,town,"Indonesia"),//38
			new Node(3050,1380,town,"New Guinea"),
			new Node(2800,1700,town,"Western Australia"),//40
			new Node(3040,1740,town,"Eastern Australia")
		};
		
		

		nodes[0].conectWithNodes(new Node[] {nodes[1],nodes[3],nodes[0],nodes[30]});
		nodes[1].conectWithNodes(new Node[] {nodes[2],nodes[3],nodes[4]});
		nodes[2].conectWithNodes(new Node[] {nodes[5],nodes[13]});
		nodes[3].conectWithNodes(new Node[] {nodes[4],nodes[6]});
		nodes[4].conectWithNodes(new Node[] {nodes[5],nodes[6],nodes[7]});
		nodes[5].conectWithNodes(new Node[] {nodes[7]});
		nodes[6].conectWithNodes(new Node[] {nodes[7],nodes[8]});
		nodes[7].conectWithNodes(new Node[] {nodes[8]});
		nodes[8].conectWithNodes(new Node[] {nodes[9]});
		
		nodes[9].conectWithNodes(new Node[] {nodes[10],nodes[11]});
		nodes[10].conectWithNodes(new Node[] {nodes[11],nodes[12]});
		nodes[11].conectWithNodes(new Node[] {nodes[12],nodes[20]});
		
		
		nodes[13].conectWithNodes(new Node[] {nodes[14]});
		nodes[14].conectWithNodes(new Node[] {nodes[16],nodes[17]});
		nodes[15].conectWithNodes(new Node[] {nodes[17],nodes[19]});
		nodes[16].conectWithNodes(new Node[] {nodes[18],nodes[20]});
		nodes[17].conectWithNodes(new Node[] {nodes[18],nodes[19]});
		nodes[18].conectWithNodes(new Node[] {nodes[19],nodes[20],nodes[21],nodes[35]});
		nodes[19].conectWithNodes(new Node[] {nodes[26],nodes[31],nodes[35]});
		
		nodes[20].conectWithNodes(new Node[] {nodes[21],nodes[22],nodes[23]});
		nodes[21].conectWithNodes(new Node[] {nodes[23],nodes[35]});
		nodes[22].conectWithNodes(new Node[] {nodes[23],nodes[24]});
		nodes[23].conectWithNodes(new Node[] {nodes[24],nodes[25],nodes[35]});
		nodes[24].conectWithNodes(new Node[] {nodes[25]});
		
		nodes[26].conectWithNodes(new Node[] {nodes[27],nodes[31],nodes[32]});
		nodes[27].conectWithNodes(new Node[] {nodes[28],nodes[29],nodes[32],nodes[33]});
		nodes[28].conectWithNodes(new Node[] {nodes[29],nodes[30]});
		nodes[29].conectWithNodes(new Node[] {nodes[30],nodes[33]});
		nodes[30].conectWithNodes(new Node[] {nodes[33],nodes[34]});
		nodes[31].conectWithNodes(new Node[] {nodes[32],nodes[35],nodes[36]});
		nodes[32].conectWithNodes(new Node[] {nodes[33],nodes[36],nodes[37]});
		nodes[33].conectWithNodes(new Node[] {nodes[34]});
		nodes[35].conectWithNodes(new Node[] {nodes[36]});
		nodes[36].conectWithNodes(new Node[] {nodes[37]});
		nodes[37].conectWithNodes(new Node[] {nodes[38]});
		
		nodes[38].conectWithNodes(new Node[] {nodes[39],nodes[40]});
		nodes[39].conectWithNodes(new Node[] {nodes[40],nodes[41]});
		nodes[40].conectWithNodes(new Node[] {nodes[41]});

		/*
		for (int i = 0;i< nodes.length;i++) {
			float result = rnd.nextFloat();
			//if (result<0.5f)nodes[i].owner = null;
			if (result<0.5f)nodes[i].owner = players[0];
			else nodes[i].owner = players[1];
		}
		*/
		
		for (int i = 0;i< nodes.length;i++) {

			nodes[i].units = (int)(rnd.nextFloat()*0+5);

		}
		/*
		nodes[0].owner = nodes[1].owner = nodes[2].owner = nodes[3].owner = players[0];
		nodes[4].owner = nodes[5].owner = players[1];
		*/
		
		world.addNodes(nodes);

		//world.addDeco(new WorldObject(3200/2,1000,1f,ground), 0);
		
		world.backgroundImage = ground;
		world.repeatX = true;
		/*
		for (int iy = 0;iy<200;iy++)
		for (int ix = 0;ix<200;ix++) 
		world.addDeco(new WorldObject(ix*64,(int)(rn.nextFloat()*128)+iy*64,mount), 1);
*/
		
		/*
		world.addDeco(new WorldObject(176,0,mount), 2);
		world.addDeco(new WorldObject(246,20,mount), 2);
		world.addDeco(new WorldObject(100,128,mount), 2);
		*/
		return world;
	}
}
