package cz.martinbrom.slimybees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BeeMutationTree {

    private final Map<String, List<BeeMutation>> mutationMap = new HashMap<>();

    public void registerMutation(BeeMutation mutation) {
        String child = mutation.getChild();
        List<BeeMutation> mutations = mutationMap.get(child);
        if (mutations != null && mutations.contains(mutation)) {
            throw new IllegalArgumentException("Cannot register a second mutation with the same parent for " + child);
        } else {
            mutations = new ArrayList<>();
        }

        mutations.add(mutation);
        mutationMap.put(child, mutations);
    }

    @Nullable
    public List<BeeMutation> getMutations(String child) {
        return mutationMap.get(child);
    }

}
