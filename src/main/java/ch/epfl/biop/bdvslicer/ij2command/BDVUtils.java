package ch.epfl.biop.bdvslicer.ij2command;

import bdv.SpimSource;
import bdv.ViewerImgLoader;
import bdv.VolatileSpimSource;
import bdv.spimdata.WrapBasicImgLoader;
import bdv.tools.brightness.ConverterSetup;
import bdv.tools.brightness.RealARGBColorConverterSetup;
import bdv.tools.transformation.TransformedSource;
import bdv.viewer.SourceAndConverter;
import mpicbg.spim.data.generic.AbstractSpimData;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.sequence.Angle;
import mpicbg.spim.data.sequence.Channel;
import net.imglib2.Volatile;
import net.imglib2.display.RealARGBColorConverter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

import java.util.List;

public class BDVUtils {

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public static void initSetups(
            final AbstractSpimData< ? > spimData,
            final List< ConverterSetup > converterSetups,
            final List<SourceAndConverter< ? >> sources )
    {
        final AbstractSequenceDescription< ?, ?, ? > seq = spimData.getSequenceDescription();
        final ViewerImgLoader imgLoader = ( ViewerImgLoader ) seq.getImgLoader();
        for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
        {
            final int setupId = setup.getId();
            final Object type = imgLoader.getSetupImgLoader( setupId ).getImageType();
            if ( RealType.class.isInstance( type ) )
                initSetupRealType( spimData, setup, ( RealType ) type, converterSetups, sources );
                //else if ( ARGBType.class.isInstance( type ) )
                //	initSetupARGBType( spimData, setup, ( ARGBType ) type, converterSetups, sources );
            else
                throw new IllegalArgumentException( "ImgLoader of type " + type.getClass() + " not supported." );
        }
    }

    private static < T extends RealType< T >, V extends Volatile< T > & RealType< V > > void initSetupRealType(
            final AbstractSpimData< ? > spimData,
            final BasicViewSetup setup,
            final T type,
            final List< ConverterSetup > converterSetups,
            final List< SourceAndConverter< ? > > sources )
    {
        if ( spimData.getSequenceDescription().getImgLoader() instanceof WrapBasicImgLoader)
        {
            initSetupRealTypeNonVolatile( spimData, setup, type, converterSetups, sources );
            return;
        }
        final double typeMin = Math.max( 0, Math.min( type.getMinValue(), 65535 ) );
        final double typeMax = Math.max( 0, Math.min( type.getMaxValue(), 65535 ) );
        final RealARGBColorConverter< V > vconverter = new RealARGBColorConverter.Imp0<>( typeMin, typeMax );// <>
        vconverter.setColor( new ARGBType( 0xffffffff ) );
        final RealARGBColorConverter< T > converter = new RealARGBColorConverter.Imp1<>( typeMin, typeMax );// <>
        converter.setColor( new ARGBType( 0xffffffff ) );

        final int setupId = setup.getId();
        final String setupName = createSetupName( setup );
        final VolatileSpimSource< T, V > vs = new VolatileSpimSource<>( spimData, setupId, setupName );
        final SpimSource< T > s = vs.nonVolatile();

        // Decorate each source with an extra transformation, that can be
        // edited manually in this viewer.
        final TransformedSource< V > tvs = new TransformedSource<>( vs );
        final TransformedSource< T > ts = new TransformedSource<>( s, tvs );

        final SourceAndConverter< V > vsoc = new SourceAndConverter<>( tvs, vconverter );
        final SourceAndConverter< T > soc = new SourceAndConverter<>( ts, converter, vsoc );

        sources.add( soc );
        converterSetups.add( new RealARGBColorConverterSetup( setupId, converter, vconverter ) );
    }

    private static < T extends RealType< T > > void initSetupRealTypeNonVolatile(
            final AbstractSpimData< ? > spimData,
            final BasicViewSetup setup,
            final T type,
            final List< ConverterSetup > converterSetups,
            final List< SourceAndConverter< ? > > sources )
    {
        final double typeMin = type.getMinValue();
        final double typeMax = type.getMaxValue();
        final RealARGBColorConverter< T > converter = new RealARGBColorConverter.Imp1<>( typeMin, typeMax );
        converter.setColor( new ARGBType( 0xffffffff ) );

        final int setupId = setup.getId();
        final String setupName = createSetupName( setup );
        final SpimSource< T > s = new SpimSource<>( spimData, setupId, setupName );

        // Decorate each source with an extra transformation, that can be
        // edited manually in this viewer.
        final TransformedSource< T > ts = new TransformedSource<>( s );
        final SourceAndConverter< T > soc = new SourceAndConverter<>( ts, converter );

        sources.add( soc );
        converterSetups.add( new RealARGBColorConverterSetup( setupId, converter ) );
    }

    private static String createSetupName( final BasicViewSetup setup )
    {
        if ( setup.hasName() )
            return setup.getName();

        String name = "";

        final Angle angle = setup.getAttribute( Angle.class );
        if ( angle != null )
            name += ( name.isEmpty() ? "" : " " ) + "a " + angle.getName();

        final Channel channel = setup.getAttribute( Channel.class );
        if ( channel != null )
            name += ( name.isEmpty() ? "" : " " ) + "c " + channel.getName();

        return name;
    }

}