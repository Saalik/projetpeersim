#!/usr/bin/env python3
from tempfile import mkstemp
from shutil import move
from os import remove
import sys
import subprocess



def main(argv):
    tmp = 20
    for i in range(20, 100, 10):
        print("\n\n############ Test with network size ",i," ############")
        create_next_conf(argv[2],  "size "+str(tmp), "size "+str(i))
        result = subprocess.Popen(['java', '-jar', argv[1], argv[2]], stdout=subprocess.PIPE, encoding="UTF-8")
        print(result.communicate()[0])
        print("############ End ############")
        tmp = i
    
    for i in range(100, 220, 20):
        print("\n\n############ Test with network size ",i," ############")
        create_next_conf(argv[2],  "size "+str(tmp), "size "+str(i))
        result = subprocess.Popen(['java', '-jar', argv[1], argv[2]], stdout=subprocess.PIPE, encoding="UTF-8")
        print(result.communicate()[0])
        print("############ End ############")
        tmp = i
    # create_next_conf(argv[2], "Strategy1InitNext", "Strategy3InitNext")
    # for i in [125, 250, 375, 500, 625, 750, 875, 1000]:
    #     print("\n\n\n############ Test with scope ",i," and SPI/SD 2 ############")
    #     create_next_conf(argv[2],  "scope "+str(tmp), "scope "+str(i))
    #     result = subprocess.run(['java', '-jar', argv[1], argv[2]])
    #     print(result)
    #     print("############ End ############")
    #     tmp = i 
    # create_next_conf(argv[2], "Strategy3InitNext", "Strategy1InitNext")
    # create_next_conf(argv[2], "scope 1000", "scope 125")
    
    print("All bench done")


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
