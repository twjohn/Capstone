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
        return "<" + ID + ", number of PA modules: " + PA + ", number of cabinets: " + Cabinets + ", number of power blocks: " + Powerblocks + ", output line size is "
                + Linesize + "\">";
    }
}