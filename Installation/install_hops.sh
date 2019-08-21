#!/bin/bash
DIRECTORY=$PWD"/hops"
mkdir -p $DIRECTORY
wget https://raw.githubusercontent.com/rhuebler/HOPS/SHH/AMPS/Resources/hops0.31.jar -O $DIRECTORY/hops.jar
wget https://raw.githubusercontent.com/rhuebler/HOPS/SHH/AMPS/Resources/MaltExtract1.5-JDK8.jar -O $DIRECTORY/MaltExtract.jar
wget https://raw.githubusercontent.com/rhuebler/HOPS/SHH/AMPS/Resources/ipak_HOPS.r
wget https://raw.githubusercontent.com/keyfm/amps/master/postprocessing.AMPS.r -O $DIRECTORY/postprocessing.AMPS.r

chmod u+x $DIRECTORY/hops.jar
chmod u+x $DIRECTORY/MaltExtract1.5.jar
chmod u+x $DIRECTORY/postprocessing.AMPS.r
chmod u+x $DIRECTORY/ipak_HOPS.r

cd  $DIRECTORY
Rscript ipak_hops.r

echo "you will still need a working version of malt"
echo "you can download it from Daniel Huson's github"
echo "ln -s ../path/to/malt-run /path/to/hops/dir/malt-run"
echo "you will also need a reference database you can either create a symlink of"
echo "the database location in the hops folder run hops with a config file"
echo "ln -s ../path/to/maltdb /path/to/hops/dir/malt-run/databse"
echo "or"
echo "add "
