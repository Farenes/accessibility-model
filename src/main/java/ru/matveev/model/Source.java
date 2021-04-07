package ru.matveev.model;

public class Source implements Runnable {

    private Switcher sink;
    private NetworkGraph graph;
    private long intense;

    public Source(Switcher sink, long intense, NetworkGraph graph) {
        this.sink = sink;
        this.intense = intense;
        this.graph = graph;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(intense);
                sink.addPacket(new Packet(graph.getPath(sink, graph.getRandSwitcher(sink))));
            } catch (Exception e) {
                System.out.println("Error while sending packet");
                e.printStackTrace();
            }
        }
    }

}
