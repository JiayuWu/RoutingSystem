package project1;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;

import java.io.ObjectInputStream;

import java.io.Serializable;

import java.util.ArrayList;

import org.rollerjm.graph.Path;
import org.rollerjm.graph.PathFinder;

//import IGraph;


//import AdjacencyMatrixGraph;
//import Path;
//import PathFinder;

public class Generator {

    public int nodeNum = 0;

    int[][] timenlink = null;//used to store lft_i.out info as [time period index][weight rounded to int]
    Link[] links = null;
    Node[] nodes = null;

    public Generator() {

        long startt = System.currentTimeMillis();
        Node[] odPair = readOD("data/input/OD.txt");
        readNodeLinkTimeInASCII("data/input/Nodes.txt", "data/input/Links.txt", "data/input/lft_i.out");
        int threshold = 100; // in meter
        String[] closetODIds = getClosestOD(threshold, odPair);
        String shortestPath = getRouting(3, closetODIds[0], closetODIds[1]);
        //output node position
        String[] nodeIds = shortestPath.split("-");
        try {
            //1. Output 1
            FileWriter fw = new FileWriter("data/output/Output_PathNodePositions.txt");
            //2. Output 2
            FileWriter fw2 = new FileWriter("data/output/Output_PathNodes.txt");


            for (int j = 0; j < nodeIds.length; j++) {
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i].nodeID.equalsIgnoreCase(nodeIds[j])) {
                        fw.write(nodes[i].nodeX + "," + nodes[i].nodeY + ";");
                        fw2.write(nodes[i].nodeID + ",");
                        break;
                    }
                }
            }
            fw.close();
            fw2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("total routing time is: " + (System.currentTimeMillis() - startt) + "ms");
    }

    /**
     * construct Dijkstra Graph based on user selection *
     */
    public String getRouting(int timeindex, String onodeStr, String dnodeStr) {
        String routeresult = "test";
        int linkNum = 0;

        if (timenlink != null) {
            linkNum = timenlink[0].length;
        }
        //set link weight to each link
        RouteGraph rgraph = new RouteGraph(nodeNum, timenlink.length, nodes);
        //System.out.println(nodes.length);

        //LetterGraphBuilder lgraph = new LetterGraphBuilder(nodeNum, allLinks.length);
        for (int i = 0; i < timenlink.length; i++) {
            for (int j = 0; j < linkNum; j++) {
                String startStr = links[j].fromNodeID;
                String endStr = links[j].toNodeID;
                rgraph.addPath(startStr, endStr, timenlink[i][j], i);
            }
        }

        PathFinder pathfinder = new PathFinder(rgraph.graph);
        //LetterGraphFinder lgraphfinder = new LetterGraphFinder(lgraph.getLetterGraph());
        Path resultpath = pathfinder.getShortestPath(onodeStr, dnodeStr, timeindex);
        routeresult = resultpath.toString();
        System.out.println(routeresult);
        return routeresult;
    }

    public Node[] readOD(String odFileName) {
        Node[] od = new Node[2];
        String str = null;
        try {
            BufferedReader odin = new BufferedReader(new FileReader(odFileName));

            //read the first line
            str = odin.readLine();

            String[] odStr = str.split(",");
            Node thenodeO = new Node();
            // origin
            thenodeO.nodeX = Double.valueOf(odStr[0]).doubleValue();
            thenodeO.nodeY = Double.valueOf(odStr[1]).doubleValue();
            od[0] = thenodeO;
            //destination
            Node thenodeD = new Node();
            thenodeD.nodeX = Double.valueOf(odStr[2]).doubleValue();
            thenodeD.nodeY = Double.valueOf(odStr[3]).doubleValue();
            od[1] = thenodeD;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return od;
    }

    public void readNodeLinkTimeInASCII(String nodeFileName, String linkFileName, String TTFileName) {

        try {
            BufferedReader nodein = new BufferedReader(new FileReader(nodeFileName));
            BufferedReader linkin = new BufferedReader(new FileReader(linkFileName));
            String str;

            int index = -1;  //first line is header in text
            while ((str = nodein.readLine()) != null) {
                if (index == -1) {
                    String[] nodeinfo = str.split(":");
                    nodeNum = Integer.valueOf(nodeinfo[1]).intValue();
                    nodes = new Node[nodeNum];
                } else {
                    String[] nodeinfo = str.split(",");
                    if (nodeinfo.length > 1) {
                        Node thenode = new Node();
                        thenode.nodeID = nodeinfo[0];
                        thenode.nodeX = Double.valueOf(nodeinfo[1]).doubleValue();
                        thenode.nodeY = Double.valueOf(nodeinfo[2]).doubleValue();
                        nodes[index] = thenode;
                    }
                }
                index++;
            }




            index = -1; // igore first line --- "[links]:1292"
            ArrayList linklist = new ArrayList();
            while ((str = linkin.readLine()) != null) {
                if (index != -1) {
                    String[] linkinfo = str.split(","); //e.g. 71738,6520,6383 -- link id, from node id, to node id
                    if (linkinfo.length == 3) {  // make sure the line is correct
                        Link thelink = new Link();
                        thelink.linkID = linkinfo[0];
                        thelink.fromNodeID = linkinfo[1];
                        thelink.toNodeID = linkinfo[2];
                        linklist.add(thelink);
                    }
                }
                index++;
            }

            links = (Link[]) linklist.toArray(new Link[0]);//new Link[0]

            //Third File
            readTimeInASCII(TTFileName);


            nodein.close();
            linkin.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Read file lft_i.out
    //Prerequisite: Links already readin
    //Result: time data read into timenlink[][] as integers (e.g. 4.2 --> 5)
    private void readTimeInASCII(String TTFileName){
        try {
            BufferedReader ttin = new BufferedReader(new FileReader(TTFileName));

            int timenumber = 27;
            int linknumber = links.length;

            String str;
            String[][] strTime = new String[linknumber][timenumber];//store info such as 4.2

            int index = 0;
            int count = 0;//number of links in the file

            timenlink = new int[timenumber][linknumber]; //info 4.2 will be stored as 5

            while ((str = ttin.readLine()) != null) {
                index++;
                if (index > 8) { // the first eight lines of code are metadata and descriptions

                    if ((index - 8) % 29 > 1) {
                        //the line is in the format of "  { 0 4.2}"
                        String[] info = str.split(" "); // the first two elements in the array are just " "
                        String[] info1 = info[4].split("}"); //info[4] is "4.2}"
                        strTime[(index - 8) / 29][count] = info1[0];
                        count++;
                    }

                    //count is in a range of [0,27]
                    if ((index - 8) % 29 == 0) {
                        count = 0;
                    }
                }
            }

            //Matrix transform
            for (int i = 0; i < linknumber; i++) {
                for (int j = 0; j < timenumber; j++) {
                    //strTime1[j][i]=strTime[i][j];
//                    System.out.print(strTime[i][j]+ "&" + i + "&" + j);
                    double traveltime = Double.parseDouble(strTime[i][j]);
                    timenlink[j][i] = (int) (traveltime) + 1;
                }
            }

            ttin.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] getClosestOD(int threshold, Node[] odPair) {
        //Node[] closetOdPairs = new Node[2];
        String[] closestODIds = new String[2];
        double dis2Ori = threshold * threshold;
        double dis2Des = threshold * threshold;
        for (int i = 0; i < nodes.length; i++) {
            double distoOriginX = odPair[0].nodeX - nodes[i].nodeX;
            double distoOriginY = odPair[0].nodeY - nodes[i].nodeY;
            double distoDesX = odPair[1].nodeX - nodes[i].nodeX;
            double distoDesY = odPair[1].nodeY - nodes[i].nodeY;
            if (distoOriginX > threshold || distoOriginY > threshold) ;
            else {  //withinBbox of origin
                double disx = nodes[i].nodeX - odPair[0].nodeX;
                double disy = nodes[i].nodeY - odPair[0].nodeY;
                double dis = disx * disx + disy * disy;
                if (dis < dis2Ori) {
                    closestODIds[0] = nodes[i].nodeID;
                    dis2Ori = dis;
                }
            }
            if (distoDesX > threshold || distoDesY > threshold)
                continue;
            else {  //withinBbox of destination
                double disx = nodes[i].nodeX - odPair[1].nodeX;
                double disy = nodes[i].nodeY - odPair[1].nodeY;
                double dis = disx * disx + disy * disy;
                if (dis < dis2Des) {
                    closestODIds[1] = nodes[i].nodeID;
                    dis2Des = dis;
                }
            }
        }
        return closestODIds;
    }

    public static void main(String[] args) {
        Generator bfileGenerator = new Generator();

    }

    //Unused methods

    /**
     * construct model for path calculation *
     */
    public void readNodeLinkTimeInBinary(String travelTimeFile) {
        try {
            /**  test: read from output (arrays) **/
            FileInputStream fis = new FileInputStream(travelTimeFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            nodes = (Node[]) obj;
            nodeNum = nodes.length;
            Object obj2 = ois.readObject();
            links = (Link[]) obj2;

            Object obj3 = ois.readObject();
            timenlink = (int[][]) obj3;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

class Position implements Serializable {
    public Position() {
    }

    String X;
    String Y;
}
