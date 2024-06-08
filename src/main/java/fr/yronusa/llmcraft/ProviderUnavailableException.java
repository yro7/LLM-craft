package fr.yronusa.llmcraft;

import fr.yronusa.llmcraft.Model.IGModelType;

public class ProviderUnavailableException extends Exception {

    IGModelType.Provider provider;

    public ProviderUnavailableException(IGModelType.Provider provider) {
        this.provider = provider;
    }

    public IGModelType.Provider getProvider() {
        return this.provider;
    }
}
