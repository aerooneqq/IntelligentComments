namespace IntelligentComments.Comments.Domain.Core.Content;

public interface IMergeableContentSegment
{
  void MergeWith(IMergeableContentSegment other);
}