using JetBrains.Application.UI.Controls.JetPopupMenu;
using JetBrains.ReSharper.Feature.Services.Occurrences;

namespace IntelligentComments.Comments.PSI.Features.Navigation;

[OccurrencePresenter(Priority = 5.0)]
public class NamedEntityOccurencePresenter : IOccurrencePresenter
{
  public bool Present(
    IMenuItemDescriptor descriptor, IOccurrence occurrence, OccurrencePresentationOptions options)
  {
    if (occurrence is not NamedEntityDeclaredElementOccurence nameOccurence) return false;

    descriptor.Text = nameOccurence.NameWithKind.Name;
    descriptor.Style = MenuItemStyle.Enabled;
    return true;
  }

  public bool IsApplicable(IOccurrence occurrence)
  {
    return occurrence is NamedEntityDeclaredElementOccurence;
  }
}