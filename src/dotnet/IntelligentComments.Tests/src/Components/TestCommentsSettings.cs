using IntelligentComments.Comments.Settings;
using JetBrains.Application;
using JetBrains.Collections.Viewable;

namespace IntelligentComments.Tests.Components;

[ShellComponent]
public class TestCommentsSettings : ICommentsSettings
{
  public IViewableProperty<bool> ExperimentalFeaturesEnabled { get; } = new ViewableProperty<bool>(true);
}