package cz.martinbrom.slimybees.core.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BeeMutationTree {

    private final Map<String, List<BeeMutation>> childLookup = new HashMap<>();
    private final Map<String, List<BeeMutation>> parentLookup = new HashMap<>();

    public void registerMutation(BeeMutation mutation) {
        String child = mutation.getChild();

        childLookup.computeIfAbsent(child, k -> new ArrayList<>()).add(mutation);

        List<BeeMutation> parentMutations = parentLookup.get(mutation.getFirstParent());
        if (parentMutations == null) {
            parentMutations = new ArrayList<>();
        } else if (parentMutations.contains(mutation)) {
            throw new IllegalArgumentException("Cannot register a mutation with the same parents and child twice!");
        }

        parentMutations.add(mutation);
        parentLookup.put(mutation.getFirstParent(), parentMutations);
    }

    @Nullable
    public List<BeeMutation> getMutationForChild(String child) {
        return childLookup.get(child);
    }

    // TODO: 18.05.21 Definitely need to test this
    @Nonnull
    public List<BeeMutation> getMutationForParents(String firstParent, String secondParent) {
        String parent, otherParent;
        if (firstParent.compareTo(secondParent) < 0) {
            parent = firstParent;
            otherParent = secondParent;
        } else {
            parent = secondParent;
            otherParent = firstParent;
        }

        List<BeeMutation> mutations = parentLookup.get(parent);
        if (mutations == null) {
            return Collections.emptyList();
        }

        // TODO: 03.06.21 Change the map key to both parents so we don't have to filter every time
        return mutations.stream()
                .filter(m -> m.getSecondParent().equals(otherParent))
                .collect(Collectors.toList());
    }

}
