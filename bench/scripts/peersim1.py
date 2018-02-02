#!/usr/bin/env python3
from tempfile import mkstemp
from shutil import move
from os import remove
import sys
import subprocess



def main(argv):
    sys.stdout = open("../benches/"+argv[0]+".bench", 'w')
    tmp = 125
    conf = '../../src/conf/confPro1.co'
    print("Portée, SPI, SD, Dt, Et/Dt, EDt/Dt")
    for i in [125, 250, 375, 500, 625, 750, 875, 1000]:
        print(i,", 1, 1, ", end='')        
        create_next_conf(conf,  "scope "+str(tmp), "scope "+str(i))
        result = subprocess.Popen(['java', '-jar',"../../projetpeersim.jar", conf], stdout=subprocess.PIPE, encoding="UTF-8")
        pp = result.communicate()[0].rstrip()
        print(pp)
        tmp = i
    create_next_conf(conf, "Strategy1InitNext", "Strategy3InitNext")
    for i in [125, 250, 375, 500, 625, 750, 875, 1000]:
        print(i,", 3, 3, ", end='')
        create_next_conf(conf,  "scope "+str(tmp), "scope "+str(i))
        result = subprocess.Popen(['java', '-jar',"../../projetpeersim.jar", conf], stdout=subprocess.PIPE, encoding="UTF-8")
        pp = result.communicate()[0].rstrip()        
        print(pp)
        tmp = i 
    create_next_conf(conf, "Strategy3InitNext", "Strategy1InitNext")
    create_next_conf(conf, "scope 1000", "scope 125")
    
    #print("All bench done")


def create_next_conf(file, oldParam, newParam):
    (new_conf, abs_path) = mkstemp()
    with open(new_conf, 'w') as new :
        with open (file, 'r') as old :
            for line in old :
                new.write(line.replace(oldParam, newParam))
    remove(file)
    move(abs_path, file)
    return


if __name__ == "__main__":
    main(sys.argv)
