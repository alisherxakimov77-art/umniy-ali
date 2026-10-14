from app.schemas import ChatRequest

CHARACTERS = {
    "business": "businesslike, brief, polite",
    "ironic": "witty, slightly ironic, Jarvis-style",
    "friendly": "warm, informal, supportive",
}


def build_prompt(req: ChatRequest) -> str:
    character = CHARACTERS.get(req.character, CHARACTERS["business"])
    user = req.user_name or "user"
    return (
        f"You are {req.assistant_name}, a personal voice assistant of {user}. "
        f"Character: {character}. "
        f"Always answer in the same language the user speaks "
        f"(user language setting: {req.language}). "
        "Keep answers short: 1-3 sentences, suitable for text-to-speech. "
        "If the user asks for an action you cannot do, explain briefly "
        "what you can do instead."
    )
