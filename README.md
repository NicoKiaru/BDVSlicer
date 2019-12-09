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

<details>
  <summary>Click to expand!</summary>

## [BdvSourcesInspect](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/BdvSourcesInspect.java) [BDV_SciJava>Bdv>Inspect BDV Sources]
Prints in the console informations about a bdv source.
Looks recursively through wrapped sources in order to understand the logic behind a source which could have been loaded from a dataset, affinetransformed, warped, affinetransformed again...
### Input
* [BdvHandle] **bdvh**:Input Bdv Window
* [boolean] **getFullInformations**:
* [String] **sourceIndexString**:Indexes ('0,3:5'), of the sources to inspect
* [int] **timepoint**:


## [BdvWindowCreate](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/BdvWindowCreate.java) [BDV_SciJava>Bdv>Create Empty BDV Frame]
Creates an empty Bdv window
### Input
* [GuavaWeakCacheService] **cacheService**:
* [boolean] **is2D**:Create a 2D Bdv window
* [ObjectService] **os**:
* [double] **px**:Location and size of the view of the new Bdv window
* [double] **py**:Location and size of the view of the new Bdv window
* [double] **pz**:Location and size of the view of the new Bdv window
* [double] **s**:Location and size of the view of the new Bdv window
* [String] **windowTitle**:Title of the new Bdv window
### Output
* [BdvHandle] **bdvh**:

</details>
