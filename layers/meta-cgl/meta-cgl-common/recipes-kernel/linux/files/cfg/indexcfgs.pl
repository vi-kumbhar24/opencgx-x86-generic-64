#!/usr/bin/perl -w
#----------------------------------------------------------------------
# indexcfgs.pl - utility to rename files in the current directory to an
#                indexed format so that a unique index number is
#                prepended to the current file name. The naming
#                template is:
#
#                   DDDDD-<name-or-short-description.cfg>
#
# Usage: create the *.cfg file you need, with any name and .cfg
#        extension. Run this script and it will automatically
#        rename your file to the indexed format above.
#
#  Author: Daniel BORNAZ <daniel.bornaz@enea.com
#    Date: 2014/04/09
# Version: 1.0
#
#----------------------------------------------------------------------

use strict;
use warnings;
use Getopt::Long;

my %args;
my $counter_indexed=0;
my $filetype="*.cfg";
my $counter_queue=0;
my $max_index=0;
my $dir="./";
my @files;
my @queue;
my $opt_strip;
my $opt_help;


#--------------------------------------------------------------------
# strip the index from the file name
#
sub removeindex($){
    my $tmpname=shift;
    my $newname;

    if($tmpname=~/^\d{5}\-.*cfg/ig){
        $newname=substr($tmpname,6);
        system("mv",$tmpname,$newname) == 0
            or die "Cannot rename $tmpname to $newname: $?";
    }else{
        return 0;
    }
}

#--------------------------------------------------------------------
# get the indexed file name, return the index
#
sub decodeindex($){
    my $tmpname=shift;

    if($tmpname=~/^\d{5}\-.*cfg/ig){
        return substr($tmpname,0,5);
    }else{
        return 0;
    }
}

#--------------------------------------------------------------------
# remove index from cfg files in current directory
#
sub strip_file_names {
    print "Start removing index from $filetype files in $dir\n";

    opendir(DIR,$dir) or die $!;

    while( my $file=readdir(DIR)){
        # retrieve cfg files (*.cfg)
        if($file=~/cfg$/gi){
            removeindex($file);
        }
    }

    closedir(DIR);
}

#--------------------------------------------------------------------
# index current directory cfg files
#
sub index_file_names {
    print "Start indexing $filetype files in $dir\n";

    opendir(DIR,$dir) or die $!;

    while( my $file=readdir(DIR)){
        # retrieve cfg files (*.cfg)
        if($file=~/cfg$/gi){
            @files=(@files,$file);
        }
    }

    closedir(DIR);


# separate indexed file names from the ones to be processed
    foreach my $file (@files){
        if($file=~/^\d{5}\-.*cfg/){
            my $crt_index=0;

            $crt_index=decodeindex($file);

            if($crt_index > $max_index){
                $max_index=$crt_index;
            }

            $counter_indexed++;
        }else{
            @queue=($file,@queue);
            $counter_queue++;
        }
    }

# set the next index number
    $max_index++;

# index the enqueued file names
    foreach my $file (@queue){
        my $newname;

        $newname=sprintf("%05d-%s",$max_index++,$file);
        system("mv",$file,$newname) == 0
            or die "Cannot rename $file to $newname: $?";
    }

    printf("$counter_queue files indexed, ".
           "$counter_indexed files already indexed. Done.\n");
}

#--------------------------------------------------------------------
# display usage help
#
sub print_usage {
    print "\n";
    print " ./indexcfgs.pl [--strip]\n";
    print "      no params: Index the *.cfg file names in current dir\n";
    print "                 to DDDDD-<original_file_name.cfg>\n";
    print "        --strip: Strips the index from the *.cfg file names ".
          "in current dir\n";
    print "\n";

    exit 0;
}


#--- Program starts here --------------------------------------------
GetOptions(\%args,
    "strip" => \$opt_strip,
    "help" => \$opt_help,
);

if(defined($opt_help)){
    print_usage();
}

if(defined($opt_strip)){
    strip_file_names();
}else{
    index_file_names();
}


