#!/usr/bin/env python3
from tempfile import mkstemp
from shutil import move
from os import remove
import sys
import subprocess
import re



def main(argv):
    sys.stdout = open("../benches/"+argv[0]+".bench", 'w')
    conf = '../../src/conf/confPro25.co'
    print("Proba: Noeuds: Att: EAtt: ER: EER")
    tp = 0.2
    for p in [0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]:
        create_next_conf(conf,  "proba "+str(tp), "proba "+str(p))
        tmp = 20
        for i in range(20, 100, 10):
            print(p," :",i, end='')        
            create_next_conf(conf,  "size "+str(tmp), "size "+str(i))
            result = subprocess.Popen(['java', '-jar',"../../projectpeersim.jar", conf], stdout=subprocess.PIPE, encoding="UTF-8")
            pp = result.communicate()[0].rstrip()
            filtered = "".join(list(filter(lambda x: not re.match(r'^\s*$', x), pp)))
            print(filtered)
            tmp = i
        for i in range(100, 220, 20):
            print(p," :",i, end='')
            create_next_conf(conf,  "size "+str(tmp), "size "+str(i))
            result = subprocess.Popen(['java', '-jar',"../../projectpeersim.jar", conf], stdout=subprocess.PIPE, encoding="UTF-8")
            pp = result.communicate()[0].rstrip()
            filtered = "".join(list(filter(lambda x: not re.match(r'^\s*$', x), pp)))
            print(filtered)
            tmp = i 
        tp = p
        create_next_conf(conf, "size 200", "size 20")
    create_next_conf(conf, "proba 1", "proba 0.2")
    
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
