# README #

### AMPS Summary ###

AMPS is a java pipeline which focus is to screen MALT data for the presence of a user specified 
list of target species. 

### How do I get set up? ###

In order to run AMPS you need Java version 9 or higher and a version of Malt 036 or higher a
nd a version of MALTExtract 1.2 or higher. 
The pipleine assumes a post processing step after MaltExtract is executed, therefore a postprocessing 
script should be included as well.
### Command Line Parameters ###

### Config File Parameters###
To use this as an example config file the explanitions have to be removed or 
use the example file from the resources folder
## Generalparameters ##

deleteRMA6=0 
removeRMA6 file after running MaltExtract with mode full optional 
use with caution 0 = false 1 = true currently not implemented

pathToMalt=/projects1/malt/versions/malt038/malt-run 
where to find the malt-run shellscript which comes with all implemetations of malt

pathToMaltExtract=/projects1/clusterhomes/huebler/RMASifter/RMAExtractor_jars/RMAExtractorBeta1.3.jar 
where to find RMAExtractor.jar try to use 1.3 or higher 

pathToPostProcessing=/path/to/postprocessing/script
where to find postprocessing note that postprecessing is currently not funtional

## Malt specific parameters ##

index=/projects1/malt/databases/indexed/index038/full-bac-genomes_2016-12
path to chosen Malt DB has to be constructed with the same Malt version that is used here ideally 
version 40 or higher

id=85.0	set minimum percent identity only matches whose PCI value 
surpasses this filter will be considered 

m=BlastN malt mode for this pipeline the mode should always be BlastN

at=SemiGlobal
alignmentType changing the alignment type will probably have negative effects
on the Authenticity criteria calculated in malt extract this value 
should only ever be changed by someone who knows what he is doing

topMalt=1 
only the indicated top scoring percentage of reads

sup=1 
minimum number of reads to support a node for the LCA algorithm

mq=100 
maximum number of matches that can be assinged to aread

verboseMalt=1 
want verbose output 0 = false 1 = true

memoryMode=load 
memoryMode again only change this value when you are very experienced with 
the malt sc and or your computation infrastructure somehow requires a differet malt mode

additionalMaltParameters="" 
add additional parameters separated with ";" not all malt parameters 
can be explicatily set with AMPS those paramters can be specified here use with caution

threadsMalt=32
set Threads for MALT when using slrum

maxMemoryMalt=500
set maximum Memory for MALT in GB!!! to use with slurm

partitionMalt=batch 
Set Optional partition for malt optional default will use batch

## MaltExtract Specific Parameters ##

filter=def_anc
filter mode uses def_anc, default, ancient, scan or crawl if in doubt use def_anc

taxas=Yersinia_pesits;Mycobacterium_tuberculosis
species names in following format Yersinia_pesits;Mycobacterium_tuberculosis
or add to a tax file containing the species name that was interesting

resources=/Users/huebler/git/RMA6Sifter/RMASifterResurgencePrime/resources/
path to NCBI.map and ncbi.tre files which can be downloaded from daniel husons Megan github page

topMaltEx=0.01
set top percent value for considered matches optional by default 0.01 only the top highest 
scoring percent  of alignmetns will be considered for read

maxLength=0 
set a maximum read length for reads only reads shorter 
than this value will be considered optional

minPIdent=0
set minimum percent identity only matches with a value 
higher than this will be considered optional

verboseMaltEx=0
want verbose output 0 = false 1 = true optional by default false

alignment=0
retrieve alignments for all mathces that pass the filtering
0 = false 1 = true optional by default false

reads=0
retrieve that fullfill filtering criteria reads 0 = false 1 = true optional

minComp=0
set minimum complexity between 0.0 and 0.8 filtering should 
Usully only strats filter when set to 0.6> or higher while anything higher then 0.7 
will be very strict but maybe test yourselves

meganSummary=0
retrieve megan summaries should be turned on this file contains the number of assingned 
reads and the taxonomic tree before any filtering was done cannot be used as input for MALTextract
but after the final analysis rma6 files can be roemeved to save space on the ard drove
when deleteRMA6 is set 1 set this to 1 as well 0 = false 1 = true optional

destackingOff=0
turn off destacking should only be used in files that are highly covered 0 = false 1 = true 
Any read that overlaps with any other read will be removed

dupRemOff=0	
turn off removal of duplicate reads 0 = false 1 = true optional

downSampOff=0	
turn off downsampling for nodes with more than 10000 
Usually we downsample to speed up computation and 10000 assigned reads is 
suffienct to detect any species in Maltextract
assigned 0 = false 1 = true optional 

threadsMaltEx=1
set number of Threads for MALT only useful with use Slurm

maxMemoryMaltEx=150
set maximum Memory in GB!!!! for MALT Extract required for use Slurm

partitionMaltEx=batch
optional set partition for maltExtract

## Post Processing Specific Parameters##
pathToList=/path/to/speciesList
specify a list with each node to target in F.M. Keys Postprocessing script

### Who do I talk to? ###

* huebler@shh.mpg.de in urgend cases
