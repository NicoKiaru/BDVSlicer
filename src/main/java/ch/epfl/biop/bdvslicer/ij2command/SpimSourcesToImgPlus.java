package ch.epfl.biop.bdvslicer.ij2command;

import ij.ImagePlus;
import net.imagej.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Spim Sources to ImgPlus")
public class SpimSourcesToImgPlus implements Command {

    @Parameter
    int source_index=0;

    @Parameter
    int time_point=0;

    @Parameter
    int level=0;

    @Parameter
    SpimSources spimSources;

    @Parameter(type = ItemIO.OUTPUT)
    ImagePlus imp;

    @Parameter
    int xmin=450;

    @Parameter
    int xmax=650;

    @Parameter
    int ymin=350;

    @Parameter
    int ymax=550;

    @Parameter
    int zmin=15;

    @Parameter
    int zmax=15;

    @Override
    public void run() {
           RandomAccessibleInterval rai = spimSources.getSources().get( source_index ).getSpimSource().getSource( time_point, level );
           RandomAccessibleInterval<RealType> view =
                   Views.interval( rai, new long[] { xmin, ymin, zmin }, new long[]{ xmax, ymax, zmax });//,  new long[]{ 500, 350 } );

           imp = ImageJFunctions.wrap(view,"BDV export ["+source_index+","+time_point+","+level+"]");
    }

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(OpenBDVCatchSources.class, true,
                "urlServer","http://fly.mpi-cbg.de:8081",
                "datasetName", "Drosophila").get();
        ij.command().run(SpimSourcesToImgPlus.class, true);
    }
}
