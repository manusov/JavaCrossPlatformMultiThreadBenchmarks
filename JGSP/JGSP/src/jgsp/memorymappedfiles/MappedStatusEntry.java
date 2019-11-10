/*
Extended status structure for memory-mapped buffer reference.
*/

package jgsp.memorymappedfiles;

import java.nio.MappedByteBuffer;
import jgsp.statistics.StatusEntry;

class MappedStatusEntry extends StatusEntry
{
final MappedByteBuffer buffer;
final MappedByteBuffer bufferDual;

MappedStatusEntry( boolean flag, String string, MappedByteBuffer buffer )
    {
    super( flag, string );
    this.buffer = buffer;
    this.bufferDual = null;
    }

MappedStatusEntry( StatusEntry entry, MappedByteBuffer buffer )
    {
    super( entry.flag, entry.string );
    this.buffer = buffer;
    this.bufferDual = null;
    }

MappedStatusEntry( boolean flag, String string, 
                   MappedByteBuffer srcBuffer, 
                   MappedByteBuffer dstBuffer )
    {
    super( flag, string );
    this.buffer = srcBuffer;
    this.bufferDual = dstBuffer;
    }

MappedStatusEntry( StatusEntry entry, 
                   MappedByteBuffer srcBuffer, 
                   MappedByteBuffer dstBuffer )
    {
    super( entry.flag, entry.string );
    this.buffer = srcBuffer;
    this.bufferDual = dstBuffer;
    }
}
