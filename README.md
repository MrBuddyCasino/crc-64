crc-64
======

This a little utility to calculate the CRC64 checksum, using the ECMA polynom. 
The original code is by Roman Nikitchenko (roman@nikitchenko.dp.ua), posted on <a href="http://stackoverflow.com/questions/20562546/how-to-get-crc64-distributed-calculation-use-its-linearity-property">StackOverflow</a>, 
using the nested lookup table design by Mark Adler for increased performance.

On my 2,3 GHz i7 MBP, I got 1150 MB/s using Java 8. The non-optimized version peaked at 375 MB/s, so thats about 3x faster.
