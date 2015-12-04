# Fuzzer
A Java-based fuzz testing application

## Background
The fuzzer is based on a lecture on fuzzing by security researcher Charlie Miller.

See the lectures here:
```
https://www.youtube.com/watch?v=Xnwodi2CBws
https://www.youtube.com/watch?v=lK5fgCvS2N4
```
Additionally, here are the five lines of code written in his lecture:
```
numwrites = random.randrange(math.ceil((float(len(buf)) / FuzzFactor))) + 1
for j in range(numwrites):
  rbyte = random.randrange(256)
  rn = random.randrange(len(buf))
  buf[rn] = "%c"%(rbyte)
```

## Usage
Simply, run the FuzzerDriver.java program to test the files provided or use the Fuzzer.java program with the following command: java Fuzzer [file location] [fuzz factor] [iterations]

## Limitations
The program has only been tested on an Apple computer. It may not work on Windows.
