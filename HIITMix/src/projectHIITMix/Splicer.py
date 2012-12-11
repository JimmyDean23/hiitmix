#file input: file, startTime, duration
import sys
import numpy
import echonest.audio as echoAudio
from pyechonest import config

def main() :
    config.ECHO_NEST_API_KEY= "GG0IO4JU1FZQJ0IH1"
    clips = file(sys.argv[1], "r")
    dataList = []
    rate = 12000
    channels = 2
    for line in clips:
        detailArray = line.split('|')
        songFile = detailArray[0]
        startTime = float(detailArray[1])
        duration = float(detailArray[2])
        endTime = startTime + duration
        n = numpy.array([])
        e = echoAudio.AudioData(songFile, n, None, rate, channels)
        q = echoAudio.AudioQuantumList(None, "segment", None, e)
        a = echoAudio.AudioAnalysis(songFile)
        segments = a.segments
        finalSeg = segments[len(segments) - 1]
        songLength = finalSeg.start + finalSeg.duration
        complete = False
        while(not complete):
            for s in segments:
                    if((s.start >= startTime) & (s.start <= endTime)):
                        q.append(s)
            if songLength >= endTime:
                complete = True
            else:
                complete = False
                endTime = endTime - songLength                            
        p = echoAudio.getpieces(e, q)
        dataList.append(p)
    clips.close()
    echoAudio.assemble(dataList, channels, rate).encode(sys.argv[2])
        
if __name__ == '__main__':
    import sys
    main()
