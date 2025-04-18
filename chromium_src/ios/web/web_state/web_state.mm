// Copyright (c) 2025 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// You can obtain one at https://mozilla.org/MPL/2.0/.

#include "src/ios/web/web_state/web_state.mm"

namespace web {
bool WebState::InterfaceBinder::IsAllowedForOrigin(
    const GURL& origin,
    std::string_view interface_name) {
  DCHECK(!origin.is_empty() && origin.is_valid());
  DCHECK(!interface_name.empty());
  if (auto it = untrusted_callbacks_.find(origin.host_piece());
      it != untrusted_callbacks_.end()) {
    return it->second == interface_name;
  }
  return false;
}
}  // namespace web
