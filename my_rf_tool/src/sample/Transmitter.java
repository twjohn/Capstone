package sample;

class Transmitter {

    private String ID;
    private int PA;
    private int Cabinets;
    private int Powerblocks;
    private String Linesize;

    public Transmitter(String ID, int PA, int Cabinets, int Powerblocks, String Linesize) {
        this.ID = ID;
        this.PA = PA;
        this.Cabinets = Cabinets;
        this.Powerblocks = Powerblocks;
        this.Linesize = Linesize;
    }

    @Override
    public String toString() {
        return "Transmitter Model: " +ID + "\nNumber of PA Modules: " + PA + "\nNumber of Cabinets: " + Cabinets + "\nNumber of Power Blocks: " + Powerblocks + "\nOutput Line Size: "
                + Linesize+"\"";
    }
}
