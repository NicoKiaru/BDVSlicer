package ch.epfl.biop.bdvslicer.ij2command;

import bdv.BigDataViewer;
import bdv.ViewerImgLoader;
import bdv.cache.CacheControl;
import bdv.export.ProgressWriterConsole;
import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.WrapBasicImgLoader;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.tools.InitializeViewerState;
import bdv.tools.brightness.ConverterSetup;
import bdv.viewer.SourceAndConverter;
import bdv.viewer.ViewerOptions;
import ij.ImagePlus;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import net.imagej.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>BDV Open with sources catching.")
public class OpenBDVCatchSources implements Command {

    @Parameter
    String urlServer;

    @Parameter
    String datasetName;

    @Parameter
    LogService ls;

    @Parameter
    ObjectService os;

    @Override
    public void run() {
       try {
            ls.info(this.getClass().toString() + " -");
            ls.info("\t - Server: "+ urlServer);
            ls.info("\t - Dataset" + datasetName);
            Map<String,String> BDSList = BDVServerUtils.getDatasetList(urlServer);
            final String xmlFilename  = BDSList.get(datasetName);

            final SpimDataMinimal spimData = new XmlIoSpimDataMinimal().load( xmlFilename );

            //final BigDataViewer bdv = BigDataViewer.open( spimData, "Allen Mouse Brain 3D", new ProgressWriterConsole(), ViewerOptions.options() );
            //if ( !bdv.tryLoadSettings( xmlFilename ) )
            //	InitializeViewerState.initBrightness( 0.001, 0.999, bdv.viewer, bdv.setupAssignments );
            if ( WrapBasicImgLoader.wrapImgLoaderIfNecessary( spimData ) )
            {
                System.err.println( "WARNING:\nOpening <SpimData> dataset that is not suited for interactive browsing.\nConsider resaving as HDF5 for better performance." );
            }

            String windowTitle = "Allen Mouse Brain 3D";
            ProgressWriterConsole progressWriter = new ProgressWriterConsole();
            ViewerOptions options = ViewerOptions.options() ;


            final ArrayList< ConverterSetup > converterSetups = new ArrayList<>();
            final ArrayList<SourceAndConverter< ? >> sources = new ArrayList<>();
            BDVUtils.initSetups( spimData, converterSetups, sources );

            final AbstractSequenceDescription< ?, ?, ? > seq = spimData.getSequenceDescription();
            final int numTimepoints = seq.getTimePoints().size();
            final CacheControl cache = ( (ViewerImgLoader) seq.getImgLoader() ).getCacheControl();

            final BigDataViewer bdv = new BigDataViewer( converterSetups, sources, spimData, numTimepoints, cache, windowTitle, progressWriter, options );

            WrapBasicImgLoader.removeWrapperIfPresent( spimData );

            bdv.getViewerFrame().setVisible( true );
            InitializeViewerState.initTransform( bdv.getViewer() );

            os.addObject(new SpimSources(sources));

        } catch (SpimDataException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(OpenBDVCatchSources.class, true,
                "urlServer","http://fly.mpi-cbg.de:8081",
                "datasetName", "Drosophila");
    }

}
