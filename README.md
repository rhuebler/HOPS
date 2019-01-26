# README #

===== HOPS Summary=====

HOPS is a java pipeline which focuses on screening MALT data (see Table of Contents) for the presence of a user-specified list of target species. The pipeline essentially exists to make it easier to use MALT and MaltExtract in unison. To use HOPS you will need a config file, which specifies some key parameters for MALT and MaltExtract. You can have multiple config files to quickly redo a previous analysis or archive them to remember parameters you used in an analysis. HOPS will in every case create a log that tells you which command was sent to Slurm, in case you encounter problems, that log file is a very good place to start looking for the problem.

For MALT check out: https://github.com/danielhuson/malt

For the hacked version of MALT with some extra functionality: https://github.com/rhuebler/cMALT
(Please use the oringal version unless you really require some of the added functions)

For the source code to MaltExtract: https://github.com/rhuebler/MaltExtract

For the source code to the postprocessing scipts: https://github.com/keyfm/amps


===== How do I get set up? ======

In order to run HOPS you need Java version 9 or higher and a version of MALT 036 or higher and a version of MaltExtract 1.2 or higher. The most up-to-date versions are available automatically, but if you need to use a previous version you can set these parameters in the config file. ** In our case you have to specify at the moment the Java version from the tools folder **

==== Example ====

<code bash> /projects1/tools/java/jdk-9.0.4/bin/java -jar /projects1/clusterhomes/huebler/RMASifter/AMPS/hops0.2.jar -input  /path/to/files/*fastq.gz -output /my/output/goes/here -m full </code>
This command will execute HOPS with default parameters, which means: submit MALT to the long queue, MaltExtract to the medium queue and post processing to the short queue and shutdown. Because this command does not really require any processing power or memory you can probably log in to cruncher1 and execute it there.

or if you want to do something more specific

<code bash> /projects1/tools/java/jdk-9.0.4/bin/java -jar /projects1/clusterhomes/huebler/RMASifter/AMPS/hops0.2.jar -input  /path/to/files/*fastq.gz -output /my/output/goes/here -m full -c ConfigFile_That_Overwrites_Rons_carefully_chosen_default_parameters.txt </code>

This will still only submit jobs to SLURM, but parameters specified in the config file will overwrite default parameters. All parameters not specified in the config file will stay at their default value. To check what the default values are, check the config file section.  These parameters can also change where the job is submitted and how many resources are reserved for it.  

===== Command Line Parameters ======

** -i --input ** Path to input files or directory, depending on which mode you use. For full or malt specify a directory with input fa, fasta , fastq or fq files which can also be gzipped or just list the files. For the maltex mode only rma6 files will be accepted as input for the post mode (still underdevelopment) a folder containing the MaltExtract output has to be used as input

** -m --mode ** which mode to use for the pipeline. Accepted values are ** full **, which runs malt, malt extract and postprocessing, ** malt **, to only execute malt, ** maltex ** to run malt extract and ** post ** to run the postprocessing 
You can use -m me_po to just run MaltExtract and post processing on MALT output 

** -c --configFile ** specify the path to a valid config file for HOPS which values are mandetory is explained in the config file section of this manual this optional and only necessary to overwrite default values.

** -o --output ** specify a valid path to the output directory best avoid all characters reserved for unix prompts

** -h --help ** print help


===== HOPS Input Files =====

To run MALT or the full mode of HOPS in any case you will need *adapter clipped or adapter clipped and merged*(in case of paired-end) data, as input. Not removing the adapter will result MALT in misalignments. [[add EAGER LINK]]

==== Configure your own Screening List for HOPS ====
To generate your own screening list you can use the taxas parameter and specify a plain text file
that has on each line the name of a species you are interested in. To do that you will have to run HOPS with a Config file.
You can check /projects1/clusterhomes/huebler/RMASifter/AMPS/reworkedPathogenListwVirusesFMK_KB_RH_1.txt for an example.
In the config file you specify it like this:

**Example:**
taxas=/projects1/clusterhomes/huebler/RMASifte/AMPS/reworkedPathogenListwVirusesFMK_KB_RH_1.txt
or
taxas=/my/folder/myfavortigebugs.txt

you can just look for one species specifically

taxas="Yersinia pestis"

or multiple

taxas="Yersinia pestis;Mycobacterium tuberculosis;Bacterium anthracis"
===== HOPS Output Files =====
==== HOPS MALT Output ====

The output from MALT are rma6 files found in the ram folder. Those are basically a compressed taxonomic tree, that contains for each Node in the tree, which read is aligned (assigned) to it. You can than inspect the output in MEGAN [[http://megan.informatik.uni-tuebingen.de/]]
====HOPS MaltExtract Output====

Access these files via command line or view it in a text editor. They are located in the ancient and default folders within the amps folder that is generated in the output folder.

For read distribution and coverage ane reference sequence is chosen to be representive. This will always be the reference with the most alignments.
This is necessary as Malt allows reads to have alignments to mutliple references. This however is only a problem for nodes that are not in the leaves of the taxonomic tree.
there is a addtional node entries file in the alignment folders to provide some indication on how other files behaved on the same node

If you use --useTopALignment flag in MaltExtract than all output except read distribution will reflect only all topalignments of these nodes,
that option is currently the default in MAltExtract1.4
=== Coverage ===
_coverageHist.txt
Taxon	Reference	0	1	2	3	4	5	6	7	8	9	10	higher
Actinomyces_oris	Actinomyces_oris	1465520	311419	310106	251124	194992	138740	99276	69300	54112	41976	27670	78682

this file is available for each input file and starts by telling the taxonomic node we are analyzing, than the reference that has the most assinged alignments. Lastly we have the number of positions
covered 0,1 ,2 .... and so on times while anything covered higher than 10 times is summarized in the higher bin. As HOPS is desinged as a screening tool and not for higher covered data. Spikes in
coverage are usually a sign of something going wrong. FOr example reads mappng to conserved regions due to reference bias

_postionsCovered.txt higher than X

Taxon	Reference	AverageCoverage	Coverge_StandardDeviation	percCoveredHigher1	percCoveredHigher2	percCoveredHigher3	percCoveredHigher4	percCoveredHigher5
Actinomyces_oris	Actinomyces_oris	0.518	2.027	0.221	0.119	0.068	0.04	0.024


=== Damage Patterns ===

_damageMismatch.txt
Node	C>T_1	C>T_2	C>T_3	C>T_4	C>T_5	C>T_6	C>T_7	C>T_8	C>T_9	C>T_10	G>A_11	G>A_12	G>A_13	G>A_14	G>A_15	G>A_16	G>A_17	G>A_18	G>A_19	G>A_20	D>V(11Substitution)_1	D>V(11Substitution)_2	D>V(11Substitution)_3	D>V(11Substitution)_4	D>V(11Substitution)_5	D>V(11Substitution)_6	D>V(11Substitution)_7	D>V(11Substitution)_8	D>V(11Substitution)_9	D>V(11Substitution)_10	H>B(11Substitution)_11	H>B(11Substitution)_12	H>B(11Substitution)_13	H>B(11Substitution)_14	H>B(11Substitution)_15	H>B(11Substitution)_16	H>B(11Substitution)_17	H>B(11Substitution)_18	H>B(11Substitution)_19	H>B(11Substitution)_20	considered_Matches
Actinomyces_oris	0.38113147410358567	0.22811386778474826	0.1265068493150685	0.10326872039506836	0.08713955623081063	0.07130964610861171	0.05629024440905629	0.05195253641386238	0.05465227490094275	0.04395488932474082	0.0512072707542051	0.05394391490537367	0.05460408904474154	0.05873861488228218	0.0754444640028164	0.08394833948339483	0.10228668941979523	0.12681842672413793	0.22373607274758697	0.3721667898497167	0.017623124047259124	0.013325395370910828	0.009091226891895756	0.007938668646919925	0.008321089167541502	0.007666419135009274	0.006591192463455534	0.007089080675899139	0.006965138291099348	0.005887792946301167	0.005970421202834361	0.006467250078655744	0.007429127731631897	0.006685473422833154	0.007647351075809306	0.008448209562207954	0.007967270735719878	0.009546741639450544	0.013146367481755575	0.017408078712948378	85817

The first value is taxonomic unit
the next 10 values are C>T substitutions in 5' direction for all alignments for all reads
the next 10 values are G>A substitutions in 3' direction for all alignments for all reads
the next 10 values are Non C>T substitutions in 5' direction for all alignments for all reads which allows us to estimate noise
the next 10 values are Non G>A substitutions in 3' direction for all alignments for all reads which allows us to estimate noise
the last node is the number of processed alignments


=== Mismatch Distribution ===

_editDistance.txt
Taxon	0	1	2	3	4	5	6	7	8	9	10	higher
Actinomyces_oris	3351	7926	11330	12708	11302	10548	8675	6875	4642	3350	2076	3974

First column is the target node, returns the editDistance Distribution for all topalingments of reads that are kept after filtering

_percentIdentity.txt

Taxon	80	85	90	95	100
Actinomyces_oris	0	13569	36455	31446	5287
First Column is the taxonomic Unit and  the next  5
columns gives the number of top alginments of filtered reads that fall into the set percent identitiy bins

== removed Reads and Alignments ==

_filterTable.txt
this file gives some summary statistics for all filtered reads and alignments.
First column is the taxonomic unit
Node	NumberOfUnfilteredReads	NumberOfFilteredReads	NumberOfUnfilteredAlignments	numberOfAlignments	turnedOn?
Actinomyces_oris	520523	520523	568637	520523	On

the next two columns are the number of reads prior to filtering and after filtering the difference between those numbers is the number of removed reads
the next two columnsare the number of alignments prior and after filtering, the difference between those aolumns is the number if removed alignments
the last column gives information on whether destacking was turned on or not (moght be bugged at the moment)

=== Read Distribution ===

_additionalNodeEntries.txt
TargetNode	01	02	03	04	05	06	07	08	09	10
Actinomyces_oris	Actinomyces_oris;_CP014232.1;_TOPREFPERCREADS100	Actinomyces_naeslundii;_AP017894.1;_TOPREFPERCREADS005	Actinomyces_oris_K20;_AB573870.1;_TOPREFPERCREADS002	Actinomyces_naeslundii;_EU621354.1;_TOPREFPERCREADS001	Actinomyces_naeslundii;_EU621259.1;_TOPREFPERCREADS001	Actinomyces_viscosus;_EU621357.1;_TOPREFPERCREADS001	Actinomyces_viscosus;_EU620893.1;_TOPREFPERCREADS001	Actinomyces_sp._Chiba101;_AP017896.1;_TOPREFPERCREADS001	Actinomyces_viscosus;_EU621241.1;_TOPREFPERCREADS000	Actinomyces_johnsonii;_EU621007.1;_TOPREFPERCREADS000

This File exits to provide a way for the user to infer how reads distirbute across multiple reference. Reads assigend higher than strain level will have alignments to multiple reference
the column are the 10 refernes with the most assinged alignments if the taxonomic unit has less than 10 references sequences the mossing entries will be replaced with a NA
Each column gives the name of the reference, and the perceantage of alignments assingend to it (when compared to the highest scoring reference)

_alignmentDist.txt
Taxon	Reference	uniquePerReference	nonStacked	nonDuplicatesonReference	TotalAlignmentsOnReference	ReferenceLength
Actinomyces_oris	Actinomyces_oris	0.197	3298	26476	86455	3042917
This file provides information on alignment distribution for the reference sequence that has the most alignments for each taxonomic unit.
The first two columns is the the name of the taxon and the name of the referece squence
the next is score that gives the fraction of bp that are covered uniquely over all the basepairs of the reads that could theoretically be uniquely mapping.
So this score gives some change to estimate overlap. the next three columns is the number of non stacking reads, of non duplicate reads and the total number of alignments assigned to the reference.
the last one is the lenght of the reference

Taxon	25bp	30bp	35bp	40bp	45bp	50bp	55bp	60bp	65bp	70bp	75bp	80bp	85bp	90bp	95bp	100bp	105bp	110bp	115bp	120bp	125bp	130bp	135bp	140bp	145bp	150bp	155bp	160bp	165bp	170bp	175bp	180bp	185bp	190bp	195bp	200bp
Actinomyces_oris	0	1917	7111	10570	11733	10835	8965	7580	6250	4657	4811	3022	2273	1835	1117	962	716	655	548	407	277	245	177	94	0	0	0	0	0	0	0	0	0	0	0	0
the last entry provides for all filtered assinged reads how read length was distributed between 20 and 200 bp 

=== RunSummary ===

This file returns for all input files how many reads are assinged to the target taxonomic units after filtering

=== TotalCount.txt ===

contains the total number of assigned reads for all files 

====HOPS Postprocessing Output====
=== Summary HeatMap ===
Contains a colour-coded heatmap for the HOPS input files. For each input file all pathogens are shown that have passed at least one criterion of post-processing. The pathogens found will be listed on the vertical axis, and the samples will be on the horizontal axis.

The colour-coding on the heat map is as follows: grey indicates absence, yellow indicates that the DNA fragment(s) match a given reference genome; orange indicates that the DNA fragment(s) match, but there are nucleotide differences, which can possibly be indicative of DNA damage patterns; red indicates that the DNA fragments match and have nucleotide differences that follow a distribution pattern that is more consistent with aDNA damage. An example heat map is provided below. Click to enlarge.

{{ :pathogens:heatmap_overview_wevid.png?200 |}}

=== pdf Reports===
More detailed information on every 'hit' so that you can review what post-processing decided and make up your mind if you want to follow up on it. For a description of each of the sections of the pdf report, please see [[https://github.com/keyfm/amps/blob/master/profilePDF_explained.pdf]]


===== Config File Parameters =====

In this Section all currently accepted parameters are explained. Please note that it is sufficient to only specify the the parameters you want to overwrite in the the config file. There is a ready to use example config file in /projects1/clusterhomes/huebler/RMASifter/amps/

To use this online example as a config file, the explanations have to be removed or be located at a new line. While this is a bit cumbersome, ** please, be aware you only need to specify parameters that you want to overwrite. There is really no need specify every parameter that is listed here!!! ** 

==== Generalparameters ====
** useSlurm ** use Slurm for scheduling. I false can be used independend from Slurm HOPS will run each step of the pipeline in succession withput scheduling to a pipeline 

** deleteRMA6=0 ** remove RMA6 files after running MaltExtract, will only be supported for full mode. optional, use with caution! 0 = false 1 = true ** currently not implemented** 

** pathToMalt=/projects1/malt/versions/malt040/malt-run ** where to find the malt-run shellscript which comes with all implementations of malt

** pathToMaltExtract=/projects1/clusterhomes/huebler/RMASifter/RMAExtractor_jars/MaltExtract1.3.jar ** where to find RMAExtractor.jar try to use 1.3 or higher

** pathToPostProcessing=/projects1/clusterhomes/huebler/RMASifter/AMPS/PostProcessing/amps-master/postprocessing.AMPS.r ** where to find the postprocessig script

** pathToPreProcessing=/projects/clusterhomes/huebler/RMASifter/AMPS/RemoveHumanReads.sh ** specify the path of a  preprocessing script. The current one removes reads mapping to human as runtimes in samples with ** high ** endogenous human DNA run a lot longer through the FullBacFullVir database.If you want  to provide your own preprocessing script it has to produce ".fq.gz" at the end

** preProcess=0 ** want to run preprocessing that removes human reads before MALT. 0 = false, 1 = true

==== MALT Parameters ====

=== MALT SLURM Parameters ===

** threadsMalt=32 ** set Threads for MALT when using slrum

** maxMemoryMalt=650 ** set maximum Memory for MALT in GB!!! to use with slurm

** partitionMalt=long ** Set Optional partition for malt optional default will use batch

** wallTimeMalt=48:00:00 ** A walltime can be set for MALT if the queue is changed from long to medium. Usually this parameter is neither set nor used. Use this only if you are sure your job can finish in 48 hours!

=== General MALT Parameters ===

** index=/projects1/malt/databases/indexed/index040/full-bac-full-vir-etal-nov_2017 **path to chosen Malt DB, has to be constructed with MALT version 38 or higher

** id=90.0 ** set minimum percent identity only matches whose PCI value surpasses this filter will be considered. Please consider that you can set a higher Percent Identity value in MaltExtract. So you can Malt with Percent Identity 85, but run MaltExtract twice with percent identity 85 and 95, for example. Which saves hard disk space.

** m=BlastN ** malt mode for this pipeline. the mode should always be BlastN if you plan to use the whole pipeline. But if you for whatever reason need to run BlastX, I made sure you still can do it from the framework of the pipeline.

** at=SemiGlobal ** alignmentType! changing the alignment type will probably have *negative effects on the Authenticity criteria calculated* in MaltExtract! This value should only ever be changed by someone who knows what he is doing!

** topMalt=1 ** Only the highest scoring percentage of alignments will be considered for the LCA algorithm. Increasing this value will result in more alignments being used for the LCA algorithm, which means reads will likely be assigned to nodes higher in the taxonomy.

** sup=1 ** minimum number of reads to support a node for the LCA algorithm. This means at least one read is necessary to support a node.

** mq=100 ** maximum number of alignment that can be assinged to a read. Lowering this value will influence the taxonomic assignment and will only be worth it in a few instances.(For example in my hacked Malt version where the LCA can be disabled)

** verboseMalt=1 ** want verbose output for MALT. 0 = false 1 = true

** memoryMode=load ** memoryMode, again only change this value when you are very experienced with the malt source code and or your computation infrastructure somehow requires a different malt mode (which is obviously not the case if you work at this institute)

** additionalMaltParameters="" ** add additional parameters separated with ";". Not all Malt parameters can be explicitly set with AMPS, but those parameters can be specified here! use with caution
 
==== MaltExtract Parameters ====

For more information on parameters use the MaltExtract Manual

=== MaltExtract Slurm Parameters ===

** threadsMaltEx=20 ** set number of Threads for MaltExtract. Should never be higher than the number of input files!

** maxMemoryMaltEx=300 ** set maximum Memory in GB!!!! for MALTExtract. 

** partitionMaltEx=medium ** optional set partition for MaltExtract. You can change to long in very big jobs. But I am not sure if it is advisable to change to short

** wallTimeME =48:00:00 ** While it is possible to decrease the Walltime for MaltExtract this should only be done in very small jobs

=== General MaltExtract Parameters ===

** filter=def_anc ** filter mode uses ** def_anc **, ** default **, ** ancient **, ** scan ** or ** crawl ** or ** srna ** (in development) if in doubt use def_anc, which gives you results for all filtered reads in the default folder and all reads that have at least one damage lesion in their first 5 bases from either end. Default returns statistics for all reads that fullfill all other filter. Ancient requires reads in addition to have one mismatching lesion in their first 5 bases from either end. Scan just returns the number of assigned reads for all nodes without any filtering. runs very fast. Crawl was designed to replace a mapping to a specific reference. Return statistics for all references that match the input species name. For example Yersinia pestis will return statistics for all Yersinia pestis references. This probably the slowest mode. SRNA returns all enrries in terminal nodes it is designed to work with the disable LCA option in cMALT

** taxas=/projects1/users/key/anc5h/soi.backup/List_of_pathogens_KB_fmk12_wViruses1.txt ** Species names in following format Yersinia_pesits;Mycobacterium_tuberculosis can be submitted to AMPS directly via the config file or add the path to a taxa file containing the species names separated by new line characters (as you did previously when calling MaltExtract directly)

** resources=/projects1/clusterhomes/huebler/RMASifter/RMA_Extractor_Resources ** path to NCBI.map and ncbi.tre files, which can be downloaded from Daniel Husons Megan github page

** topMaltEx=0.01 ** set top percent value for considered matches. Only the top one percent of matches will be used by default. 

** maxLength=0 ** set a maximum read length. Only reads shorter than this value will be considered (optional). Probably only useful for def_anc or ancient.

** minPIdent=0 ** set minimum percent identity. Only matches with a value higher than this will be considered. (optional) You can be more strict in MaltExtract than you were in MALT

** verboseMaltEx=0 ** want verbose output. (0 = false 1 = true, optional ) by default false

** alignment=0 ** retrieve alignments for all matches that pass the filtering ( 0 = false 1 = true, optional )by default false

** reads=0 ** retrieve reads that fullfill filtering criteria ( 0 = false 1 = true, optional)

** minComp=0 **set minimum complexity between 0.0 and 0.8. Filtering should usually only start when set to 0.6> or higher. While anything higher then 0.7 will be very strict. But maybe test for yourselves

** meganSummary=0 ** retrieve megan summaries. Which is a file that contains the tree structure and the number of assigned reads for each node but no alignments. This should probably be turned on if clean up is enabled so that you have some idea what the Malt Files originally contained. By default disabled.

** destackingOff=0 **turn off destacking. Should only be used in files that are highly covered! (0 = false 1 = true) Otherwise any read that overlaps with any other read will be removed

** dupRemOff=0 ** turn off removal of duplicate reads (0 = false 1 = true, optional)

** downSampOff=0** turn off downsampling for nodes with more than 10000 assinged reads. Usually we downsample to speed up computation and 10000 assigned reads is sufficient to detect any species in Maltextract!( 0 = false 1 = true, optional)

** useTopAlignment=0 ** turn on to only use the top alignment for every statistic, except for those concerning read distribution and coverage

==== PostProcessing Parameters ====

=== PostProcessing Slurm Parameters ===
** threadsPost=2 ** set number of threads to use in postprocessing

** partitionPost=short ** set partition for postprocessing

** maxMemoryPost=10 ** set Max Memory for Postprocessing

** wallTimePost=1:00:00 ** set walltime for postprocessing

=== PostProcessing GeneralParameters ===

** pathToList=/projects1/users/key/anc5h/soi.backup/List_of_pathogens_KB_fmk12_wViruses1.txt**  specify path to postprocessing node list

=== PreProcessing Slurm Parameters ===
** wallTimePreProcessing= 48:00:00 ** Set walltime for preprocessing will only be used if partition is changed to medium or short but this only a viable option in small jobs

** partitionPreProcessing=long ** by default preproceeing will queue in long queue but you can change this

** threadsPreProcessing=32 ** number of treads used for preprocessing, by default 32 threads will be used. As the shellscript itself will only require 32 cores, this parameter should only be changed when you also change the preprocessing script itself

** memoryPreProcessing=500 ** change the number reserved memory for preprocessing

