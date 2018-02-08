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

## Generalparameters ##
#removeRMA6 file after running MaltExtract with mode full optional 
#use with caution 0 = false 1 = true currently not implemented
deleteRMA6=0 

#where to find the malt-run shellscript which comes with all implemetations of malt
pathToMalt=/projects1/malt/versions/malt038/malt-run

#where to find RMAExtractor.jar try to use 1.3 or higher 
pathToMaltExtract=/projects1/clusterhomes/huebler/RMASifter/RMAExtractor_jars/RMAExtractorBeta1.3.jar 

#where to find postprocessing note that postprecessing is currently not funtional
pathToPostProcessing=Z

## Malt specific parameters ##
#path to chosen Malt DB has to be constructed with the same Malt version that is used here
#ideally version 40 or higher
index=/projects1/malt/databases/indexed/index038/full-bac-genomes_2016-12

#set minimum percent identity only matches whose 
#PCI value supasses this filter will be considered 
id=85.0

#malt mode for this pipeline the mode should always be BlastN
m=BlastN

#alignmentType changing the alignment type will probably have 
#negative effects on the Authenticity criteria calculated in malt extract
#this value should only ever be changed by someone who knows what he is doing
at=SemiGlobal

#only the indicated top scoring percentage of reads  
topMalt=1

#minimum number of reads to support a node for the LCA algorithm
sup=1

#maximum number of matches that can be assinged to aread
mq=100

#want verbose output 0 = false 1 = true
verboseMalt=1

#memoryMode again only change this value when you are very experienced with the malt sc 
#and or your computation infrastructure somehow requires a differet malt mode
memoryMode=load

#add additional parameters separated with ;
#not all malt parameters can be explicatily set with AMPS those paramters can be specified here
#use with caution
#additionalMaltParameters=""

#set Threads for MALT when using slrum
threadsMalt=32

#set maximum Memory for MALT in GB!!! to use with slurm
maxMemoryMalt=500

#Set Optional partition for malt optional default will use batch
partitionMalt=batch 


## MaltExtract Specific Parameters ##

#filter mode uses def_anc, default, ancient, scan or crawl if in doubt use def_anc
filter=def_anc

#species names in following format Yersinia_pesits;Mycobacterium_tuberculosis
#or add to a tax file containing the species name that was interesting
taxas=Yersinia_pesits;Mycobacterium_tuberculosis

#path to NCBI.map and ncbi.tre files which can be downloaded from daniel husons Megan github page
resources=/Users/huebler/git/RMA6Sifter/RMASifterResurgencePrime/resources/

#set top percent value for considered matches optional by default t0.01
topMaltEx=0.01

#set a maximum read length for reads only reads shorter 
#than this value will be considered optional
maxLength=0 

#set minimum percent identity only matches with a value 
#higher than this will be considered optional
minPIdent=0

#want verbose output 0 = false 1 = true optional by default false
verboseMaltEx=0

#retrieve alignments for all mathces that pass the filtering
#0 = false 1 = true optional by default false
alignment=0

#retrieve that fullfill filtering criteria reads 0 = false 1 = true optional
reads=0

#set minimum complexity between 0.0 and 0.8 filtering should 
#only start for 0.6> or higher while anything higher then 0.7 will be very strict
minComp=0

#retrieve megan summaries should be turned on 
#when deleteRMA6 is set 0 = false 1 = true optional
meganSummary=0

#turn off destacking should only be used in files that are highly covered 0 = false 1 = true 
destackingOff=0

#turn of duplicate removal 0 = false 1 = true optional
dupRemOff=0

#turn off downsampling for nodes with more than 10000 
#assigned 0 = false 1 = true optional to speed up analysis
downSampOff=0

#set number of Threads for MALT only useful with use Slurm
threadsMaltEx=16

#set maximum Memory in GB!!!! for MALT Extract required for use Slurm
maxMemoryMaltEx=150

#Set Optional partition for maltEx
partitionMaltEx=batch 

#PostProcessing
pathToList=///
### Who do I talk to? ###

* huebler@shh.mpg.de in urgend cases
