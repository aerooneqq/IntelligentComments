using IntelligentComments.Comments.Settings;
using JetBrains.Annotations;
using JetBrains.Application;
using JetBrains.Collections.Viewable;
using JetBrains.Lifetimes;
using JetBrains.Rider.Model;

namespace IntelligentComments.Rider.Comments.Settings;

[ShellComponent]
public class RiderCommentsSettings : ICommentsSettings
{
  public IViewableProperty<bool> ExperimentalFeaturesEnabled { get; }

  
  public RiderCommentsSettings(Lifetime lifetime, [NotNull] ShellModel shellModel)
  {
    ExperimentalFeaturesEnabled = new ViewableProperty<bool>();
    shellModel.GetRdCommentsSettingsModel().EnableExperimentalFeatures.Advise(lifetime, HandleNewExperimentalValue);
  }

  
  private void HandleNewExperimentalValue(bool enableExperimentalFeatures)
  {
    ExperimentalFeaturesEnabled.Value = enableExperimentalFeatures;
  }
}