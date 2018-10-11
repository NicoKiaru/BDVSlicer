package ch.epfl.biop.bdvslicer.ij2command;

import bdv.viewer.SourceAndConverter;

import java.util.ArrayList;

public class SpimSources {

    public SpimSources(ArrayList<SourceAndConverter< ? >> sources) {
        this.sources = sources;

    }

    public ArrayList<SourceAndConverter< ? >> getSources() {
        return sources;
    }

    ArrayList<SourceAndConverter< ? >> sources;
}
