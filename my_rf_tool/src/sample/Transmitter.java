package sample;

class Transmitter {

    private String ID;
    private int PA;
    private int Cabinets;
    private int Powerblocks;
    private String Linesize;
    private String filter;

    public Transmitter(String ID, int PA, int Cabinets, int Powerblocks, String Linesize) {
        this.ID = ID;
        this.PA = PA;
        this.Cabinets = Cabinets;
        this.Powerblocks = Powerblocks;
        this.Linesize = Linesize;
    }
    public String Filters(String filter) {
        return this.filter = filter;
    }

    @Override
    public String toString() {
        return "Transmitter Model: " +ID + "\nNumber of PA Modules: " + PA + "\nNumber of Cabinets: " + Cabinets + "\nNumber of Power Blocks: " + Powerblocks + "\nOutput Line Size: "
                + Linesize+"\"";
    }
}
