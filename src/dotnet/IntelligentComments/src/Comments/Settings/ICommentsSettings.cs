using JetBrains.Annotations;
using JetBrains.Collections.Viewable;

namespace IntelligentComments.Comments.Settings;

public interface ICommentsSettings
{
  [NotNull] IViewableProperty<bool> ExperimentalFeaturesEnabled { get; }
}