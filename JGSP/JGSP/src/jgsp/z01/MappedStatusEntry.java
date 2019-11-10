package jgsp.z01;

import java.nio.MappedByteBuffer;
import jgsp.statistics.StatusEntry;

public class MappedStatusEntry extends StatusEntry
{
public final MappedByteBuffer buffer;
public final MappedByteBuffer bufferDual;

public MappedStatusEntry( boolean flag, String string, MappedByteBuffer buffer )
    {
    super( flag, string );
    this.buffer = buffer;
    this.bufferDual = null;
    }

public MappedStatusEntry( StatusEntry entry, MappedByteBuffer buffer )
    {
    super( entry.flag, entry.string );
    this.buffer = buffer;
    this.bufferDual = null;
    }

public MappedStatusEntry( boolean flag, String string, 
                          MappedByteBuffer srcBuffer, 
                          MappedByteBuffer dstBuffer )
    {
    super( flag, string );
    this.buffer = srcBuffer;
    this.bufferDual = dstBuffer;
    }

public MappedStatusEntry( StatusEntry entry, 
                          MappedByteBuffer srcBuffer, 
                          MappedByteBuffer dstBuffer )
    {
    super( entry.flag, entry.string );
    this.buffer = srcBuffer;
    this.bufferDual = dstBuffer;
    }

}
