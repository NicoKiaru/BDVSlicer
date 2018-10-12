ImageJ2 command which returns an ImgPlus object from a BigDataViewer instance.

BDVSliceToImgPlus takes the current view and returns an ImagePlus image.

The user needs to specify:
* The interval in X Y and Z (Z = 0 means a single slice)
* Interpolation
* Source index

The orientation and position and timepoint within the dataset is copied from the BigBataViewer object.

Potential improvements:
* A command that takes an already opened BigDataViewer and stores it into an object service; right now the slicer is only able to retrieve the BigDataViewer object if it has been put into an ObjectService
* An automated bounding box computing based on the transformation to retrieve the whole source slice


For discussion see on forum.image.sc https://forum.image.sc/t/bigdataviewer-bigdataserver-get-an-imageplus-image-of-the-current-slice/20138
