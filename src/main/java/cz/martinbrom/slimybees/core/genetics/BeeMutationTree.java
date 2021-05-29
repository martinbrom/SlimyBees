package cz.martinbrom.slimybees.core.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BeeMutationTree {

    private final Map<String, BeeMutation> mutationMap = new HashMap<>();
    private final Map<String, List<BeeMutation>> parentLookup = new HashMap<>();

    public void registerMutation(BeeMutation mutation) {
        String child = mutation.getChild();
        if (mutationMap.containsKey(child)) {
            throw new IllegalArgumentException("Cannot register a second mutation for " + child + "!");
        }

        mutationMap.put(child, mutation);

        List<BeeMutation> parentMutations = parentLookup.get(mutation.getFirstParent());
        if (parentMutations == null) {
            parentMutations = new ArrayList<>();
        } else if (parentMutations.contains(mutation)) {
            throw new IllegalArgumentException("Cannot register a mutation with the same parents twice!");
        }

        parentMutations.add(mutation);
        parentLookup.put(mutation.getFirstParent(), parentMutations);
        mutationMap.put(child, mutation);
    }

    @Nullable
    public BeeMutation getMutationForChild(String child) {
        return mutationMap.get(child);
    }

    // TODO: 18.05.21 Definitely need to test this
    @Nullable
    public BeeMutation getMutationForParents(String firstParent, String secondParent) {
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
            return null;
        }

        if (mutations.size() == 1) {
            return mutations.get(0);
        }

        return mutations.stream()
                .filter(m -> m.getSecondParent().equals(otherParent))
                .findFirst()
                .orElse(null);
    }

}
